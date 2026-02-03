package com.health.service;

import com.health.entity.PasswordResetToken;
import com.health.entity.User;
import com.health.repository.PasswordResetTokenRepository;
import com.health.repository.UserRepository;
import com.health.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication service handling user registration, login, and password reset.
 */
@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final String RESET_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // No O, 0, 1, I
    private static final int RESET_CODE_LENGTH = 6;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Register a new user
     */
    @Transactional
    public Map<String, Object> register(String email, String password, String name, boolean rememberMe) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }
        
        // Validate password - require 8+ characters with complexity
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            throw new RuntimeException(passwordError);
        }
        
        // Create and save user
        User user = new User(email, passwordEncoder.encode(password), name);
        user = userRepository.save(user);
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), rememberMe);
        
        // Send welcome email (async)
        try {
            emailService.sendWelcomeEmail(email, name);
        } catch (Exception e) {
            logger.warn("Failed to send welcome email: {}", e.getMessage());
        }
        
        logger.info("New user registered: {}", email);
        
        return buildAuthResponse(user, token);
    }
    
    /**
     * Authenticate user and return JWT token
     */
    public Map<String, Object> login(String email, String password, boolean rememberMe) {
        Optional<User> optUser = userRepository.findByEmail(email);
        
        if (optUser.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }
        
        User user = optUser.get();
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), rememberMe);
        
        logger.info("User logged in: {}", email);
        
        return buildAuthResponse(user, token);
    }
    
    /**
     * Get user by ID (for token validation)
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Initiate password reset - generate and send code
     */
    @Transactional
    public Map<String, Object> initiatePasswordReset(String email) {
        Optional<User> optUser = userRepository.findByEmail(email);
        
        if (optUser.isEmpty()) {
            // Don't reveal if email exists - return success anyway for security
            logger.warn("Password reset requested for non-existent email: {}", email);
            return Map.of(
                "success", true,
                "message", "If this email exists, a reset code has been sent"
            );
        }
        
        User user = optUser.get();
        
        // Delete any existing tokens for this user
        tokenRepository.deleteByUser(user);
        
        // Generate a new reset code
        String resetCode = generateResetCode();
        
        // Save token to database
        PasswordResetToken token = new PasswordResetToken(resetCode, user);
        tokenRepository.save(token);
        
        // Send email with code
        try {
            emailService.sendPasswordResetEmail(email, resetCode, user.getName());
        } catch (Exception e) {
            logger.error("Failed to send password reset email: {}", e.getMessage());
            throw new RuntimeException("Failed to send reset email. Please try again.");
        }
        
        logger.info("Password reset initiated for: {}", email);
        
        // Return success without exposing the reset code
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Reset code sent to your email");
        
        return response;
    }
    
    /**
     * Reset password using the code
     */
    @Transactional
    public Map<String, Object> resetPassword(String code, String newPassword) {
        // Find the token
        Optional<PasswordResetToken> optToken = tokenRepository.findByTokenAndUsedFalse(code.toUpperCase());
        
        if (optToken.isEmpty()) {
            throw new RuntimeException("Invalid or expired reset code");
        }
        
        PasswordResetToken token = optToken.get();
        
        // Check if expired
        if (!token.isValid()) {
            throw new RuntimeException("Reset code has expired. Please request a new one.");
        }
        
        // Validate new password
        String passwordError = validatePassword(newPassword);
        if (passwordError != null) {
            throw new RuntimeException(passwordError);
        }
        
        // Update user password
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Mark token as used
        token.setUsed(true);
        tokenRepository.save(token);
        
        logger.info("Password reset completed for: {}", user.getEmail());
        
        return Map.of(
            "success", true,
            "message", "Password reset successfully. You can now log in."
        );
    }
    
    /**
     * Clean up expired tokens (can be called by a scheduler)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        logger.info("Expired password reset tokens cleaned up");
    }
    
    // ============ PRIVATE HELPERS ============
    
    private String generateResetCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(RESET_CODE_LENGTH);
        
        for (int i = 0; i < RESET_CODE_LENGTH; i++) {
            code.append(RESET_CODE_CHARS.charAt(random.nextInt(RESET_CODE_CHARS.length())));
        }
        
        return code.toString();
    }
    
    /**
     * Validate password strength
     * Returns error message if invalid, null if valid
     */
    private String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters";
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        
        if (!hasUpper || !hasLower || !hasDigit) {
            return "Password must contain uppercase, lowercase, and a number";
        }
        
        return null; // Valid
    }
    
    private Map<String, Object> buildAuthResponse(User user, String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "name", user.getName() != null ? user.getName() : ""
        ));
        return response;
    }
}
