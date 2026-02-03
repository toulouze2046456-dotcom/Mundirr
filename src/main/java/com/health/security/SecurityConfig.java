package com.health.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security configuration for JWT-based authentication.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private RateLimitFilter rateLimitFilter;
    
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (not needed for stateless JWT auth)
            .csrf(csrf -> csrf.disable())
            
            // Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Stateless session management (no server-side sessions)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/forgot-password").permitAll()
                .requestMatchers("/api/auth/reset-password").permitAll()
                .requestMatchers("/api/auth/logout").permitAll()
                
                // Allow OPTIONS requests for CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Health check endpoint
                .requestMatchers("/api/health").permitAll()
                
                // Public scientific data endpoints (no auth needed for constants)
                .requestMatchers("/api/hormonal/constants").permitAll()
                .requestMatchers("/api/hormonal/caffeine/drinks").permitAll()
                .requestMatchers("/api/exercises/muscles").permitAll()
                .requestMatchers("/api/exercises/equipment").permitAll()
                .requestMatchers("/api/exercises/categories").permitAll()
                .requestMatchers("/api/exercises/search").permitAll()
                .requestMatchers("/api/exercises/muscle/**").permitAll()
                .requestMatchers("/api/exercises/**").permitAll()
                .requestMatchers("/api/cultural/categories").permitAll()
                
                // Food/Nutrition search endpoints (public for browsing)
                .requestMatchers("/api/food/search").permitAll()
                .requestMatchers("/api/food/details/**").permitAll()
                
                // Finance market data (public, proxies external APIs)
                .requestMatchers("/api/finance/markets").permitAll()
                .requestMatchers("/api/finance/markets/history").permitAll()
                
                // All other API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                
                // Allow all other requests (static resources, etc.)
                .anyRequest().permitAll()
            )
            
            // Add rate limiting filter first (before any auth processing)
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse allowed origins from config
        // Alpha testing: Support wildcard "*" for all origins
        if ("*".equals(allowedOrigins)) {
            // Allow ALL origins - explicitly add capacitor for iOS Capacitor apps
            configuration.addAllowedOrigin("*");
            configuration.setAllowCredentials(false); // Must be false when using wildcard
        } else {
            List<String> origins = Arrays.asList(allowedOrigins.split(","));
            configuration.setAllowedOriginPatterns(origins);
            configuration.setAllowCredentials(true);
        }
        
        // Allowed methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allowed headers - allow ALL headers for mobile app compatibility
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Exposed headers (headers that client can access)
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type"
        ));
        
        // How long the preflight response can be cached
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
