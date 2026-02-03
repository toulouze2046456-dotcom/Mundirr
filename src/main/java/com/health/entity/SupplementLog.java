package com.health.entity;

import jakarta.persistence.*;

/**
 * Log for tracking supplement intake by date.
 */
@Entity
@Table(name = "supplement_logs")
public class SupplementLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long supplementId;
    
    @Column(nullable = false)
    private String date;
    
    private boolean taken;
    
    // Constructors
    public SupplementLog() {}
    
    public SupplementLog(Long userId, Long supplementId, String date, boolean taken) {
        this.userId = userId;
        this.supplementId = supplementId;
        this.date = date;
        this.taken = taken;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getSupplementId() { return supplementId; }
    public void setSupplementId(Long supplementId) { this.supplementId = supplementId; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public boolean isTaken() { return taken; }
    public void setTaken(boolean taken) { this.taken = taken; }
}
