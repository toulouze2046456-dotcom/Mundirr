package com.health.controller;

import com.health.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Base controller with helper methods for authenticated endpoints.
 */
public abstract class BaseController {
    
    /**
     * Extract the authenticated user from the request.
     * The JwtAuthenticationFilter sets this attribute.
     */
    protected User getAuthenticatedUser(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return user;
    }
    
    /**
     * Extract the authenticated user's ID from the request.
     */
    protected Long getAuthenticatedUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userId;
    }
}
