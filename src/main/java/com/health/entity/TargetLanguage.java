package com.health.entity;

import jakarta.persistence.*;

/**
 * Target language for cultural/language learning tracking.
 */
@Entity
@Table(name = "target_languages")
public class TargetLanguage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String name;
    
    private String level; // "Beginner", "Intermediate", "Advanced"
    private int practiceMinutes;
    
    // Constructors
    public TargetLanguage() {}
    
    public TargetLanguage(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    
    public int getPracticeMinutes() { return practiceMinutes; }
    public void setPracticeMinutes(int minutes) { this.practiceMinutes = minutes; }
}
