package com.health.security;

import com.health.entity.User;
import com.health.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * JWT Authentication Filter.
 * Intercepts requests and validates JWT tokens from Authorization header.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        logger.info("Processing request: {} {} - Auth header present: {}", 
            request.getMethod(), request.getRequestURI(), authHeader != null);
        
        // Skip if no Authorization header or not Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No Bearer token found, continuing without authentication");
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            final String jwt = authHeader.substring(7);
            logger.info("JWT token received (first 20 chars): {}...", 
                jwt.length() > 20 ? jwt.substring(0, 20) : jwt);
            
            // Validate token
            if (jwtUtil.isValidToken(jwt)) {
                Long userId = jwtUtil.extractUserId(jwt);
                String email = jwtUtil.extractEmail(jwt);
                
                logger.info("JWT validated successfully - userId: {}, email: {}", userId, email);
                
                // Only set authentication if not already set
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    
                    Optional<User> optUser = userRepository.findById(userId);
                    
                    if (optUser.isPresent()) {
                        User user = optUser.get();
                        
                        // Create authentication token
                        UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                user, // Principal - the user object
                                null, // Credentials - not needed after authentication
                                Collections.emptyList() // Authorities/Roles
                            );
                        
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // Set the authentication in context
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        // Store user in request attribute for easy access in controllers
                        request.setAttribute("user", user);
                        request.setAttribute("userId", user.getId());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("JWT validation failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Skip filter for public endpoints
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/register") ||
               path.startsWith("/api/auth/forgot-password") ||
               path.startsWith("/api/auth/reset-password");
    }
}
