package com.health.entity;

import jakarta.persistence.*;

/**
 * Sleep tracking log with quality assessment.
 */
@Entity
@Table(name = "sleep_logs")
public class SleepLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    private double hours;
    private String quality; // "Excellent", "Good", "Fair", "Poor"
    
    @Column(nullable = false)
    private String date;
    
    private int score;
    private String scientificFeedback;
    private String consequenceType;
    
    // Constructors
    public SleepLog() {}
    
    public SleepLog(Long userId, double hours, String quality, String date, int score, 
                    String scientificFeedback, String consequenceType) {
        this.userId = userId;
        this.hours = hours;
        this.quality = quality;
        this.date = date;
        this.score = score;
        this.scientificFeedback = scientificFeedback;
        this.consequenceType = consequenceType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }
    
    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public String getScientificFeedback() { return scientificFeedback; }
    public void setScientificFeedback(String feedback) { this.scientificFeedback = feedback; }
    
    public String getConsequenceType() { return consequenceType; }
    public void setConsequenceType(String type) { this.consequenceType = type; }
}
