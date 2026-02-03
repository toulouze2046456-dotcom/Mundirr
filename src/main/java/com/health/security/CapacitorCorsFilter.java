package com.health.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom CORS filter to handle Capacitor/Ionic origins that Spring's default CORS doesn't support.
 * This filter runs BEFORE Spring Security to ensure CORS headers are set for all requests.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CapacitorCorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String origin = request.getHeader("Origin");
        
        // Allow all origins during development (including capacitor://, ionic://, etc.)
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, Origin, X-Requested-With");
        response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
