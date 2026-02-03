package com.health.entity;

import jakarta.persistence.*;

/**
 * Substance tracking log (cigarettes, etc.)
 */
@Entity
@Table(name = "substance_logs")
public class SubstanceLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String type; // "Cigarettes", "Cannabis", etc.
    
    private int count;
    
    @Column(nullable = false)
    private String date;
    
    // Constructors
    public SubstanceLog() {}
    
    public SubstanceLog(Long userId, String type, int count, String date) {
        this.userId = userId;
        this.type = type;
        this.count = count;
        this.date = date;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
