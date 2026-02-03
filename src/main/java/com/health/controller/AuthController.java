package com.health.controller;

import com.health.entity.User;
import com.health.security.JwtUtil;
import com.health.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for authentication endpoints.
 * Handles registration, login, logout, and password reset.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        try {
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            String name = (String) body.getOrDefault("name", "");
            boolean rememberMe = Boolean.TRUE.equals(body.get("rememberMe"));
            
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }
            
            Map<String, Object> result = authService.register(email, password, name, rememberMe);
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Login user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        try {
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            boolean rememberMe = Boolean.TRUE.equals(body.get("rememberMe"));
            
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }
            
            Map<String, Object> result = authService.login(email, password, rememberMe);
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            logger.warn("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }
    
    /**
     * Get current user info
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Not authenticated"));
            }
            
            String token = authHeader.substring(7);
            
            if (!jwtUtil.isValidToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }
            
            Long userId = jwtUtil.extractUserId(token);
            Optional<User> optUser = authService.getUserById(userId);
            
            if (optUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found"));
            }
            
            User user = optUser.get();
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName() != null ? user.getName() : ""
            ));
            
        } catch (Exception e) {
            logger.error("Error getting user info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication error"));
        }
    }
    
    /**
     * Logout user (client should discard the token)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // With JWT, logout is handled client-side by discarding the token
        // Server-side we just return success
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }
    
    /**
     * Initiate password reset - sends code to email
     * POST /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            
            Map<String, Object> result = authService.initiatePasswordReset(email);
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            logger.error("Password reset initiation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Reset password using the code
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            String newPassword = body.get("newPassword");
            
            if (token == null || token.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Reset code is required"));
            }
            if (newPassword == null || newPassword.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "New password is required"));
            }
            
            Map<String, Object> result = authService.resetPassword(token, newPassword);
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            logger.warn("Password reset failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Refresh JWT token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No token provided"));
            }
            
            String oldToken = authHeader.substring(7);
            
            // Validate the old token (allow even if close to expiry)
            Long userId = jwtUtil.extractUserId(oldToken);
            String email = jwtUtil.extractEmail(oldToken);
            
            if (userId == null || email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }
            
            // Generate new token
            String newToken = jwtUtil.generateToken(userId, email, false);
            
            return ResponseEntity.ok(Map.of(
                "token", newToken,
                "expiresIn", jwtUtil.getRemainingValidity(newToken)
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token refresh failed"));
        }
    }
}
