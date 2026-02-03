package com.health.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Rate limiting filter for authentication endpoints.
 * Prevents brute force attacks by limiting requests per IP.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    
    // Max attempts per IP within the time window
    private static final int MAX_ATTEMPTS_PER_MINUTE = 10;
    private static final int MAX_ATTEMPTS_PASSWORD_RESET = 3;
    
    // Cache to track request counts per IP
    private final LoadingCache<String, Integer> requestCountsCache;
    private final LoadingCache<String, Integer> passwordResetCache;
    
    public RateLimitFilter() {
        // Cache entries expire after 1 minute
        this.requestCountsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public Integer load(@NonNull String key) {
                    return 0;
                }
            });
        
        // Password reset has stricter limits - 5 minute window
        this.passwordResetCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public Integer load(@NonNull String key) {
                    return 0;
                }
            });
    }
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Only rate limit auth endpoints
        if (!isAuthEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String clientIp = getClientIP(request);
        
        try {
            // Check password reset rate limit (stricter)
            if (path.contains("forgot-password") || path.contains("reset-password")) {
                int resetAttempts = passwordResetCache.get(clientIp);
                if (resetAttempts >= MAX_ATTEMPTS_PASSWORD_RESET) {
                    logger.warn("Password reset rate limit exceeded for IP: {}", clientIp);
                    sendRateLimitResponse(response, "Too many password reset attempts. Please wait 5 minutes.");
                    return;
                }
                passwordResetCache.put(clientIp, resetAttempts + 1);
            }
            
            // Check general auth rate limit
            int attempts = requestCountsCache.get(clientIp);
            if (attempts >= MAX_ATTEMPTS_PER_MINUTE) {
                logger.warn("Auth rate limit exceeded for IP: {}", clientIp);
                sendRateLimitResponse(response, "Too many requests. Please wait a minute.");
                return;
            }
            
            requestCountsCache.put(clientIp, attempts + 1);
            
        } catch (ExecutionException e) {
            logger.error("Rate limit cache error: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isAuthEndpoint(String path) {
        return path.startsWith("/api/auth/");
    }
    
    private String getClientIP(HttpServletRequest request) {
        // Check for proxy headers
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take the first IP in the chain (original client)
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
    
    private void sendRateLimitResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
