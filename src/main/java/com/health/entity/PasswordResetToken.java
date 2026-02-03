package com.health.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for storing password reset tokens.
 * Tokens expire after a configurable time (default 1 hour).
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    
    private static final int EXPIRATION_HOURS = 1;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    @Column(nullable = false)
    private boolean used = false;
    
    // Constructors
    public PasswordResetToken() {}
    
    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
        this.used = false;
    }
    
    public PasswordResetToken(String token, User user, int expirationMinutes) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusMinutes(expirationMinutes);
        this.used = false;
    }
    
    // Check if token is expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    // Check if token is valid (not expired and not used)
    public boolean isValid() {
        return !isExpired() && !used;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
