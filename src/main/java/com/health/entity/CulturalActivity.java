package com.health.entity;

import jakarta.persistence.*;

/**
 * Cultural activity tracking (movies, exhibits, etc.)
 */
@Entity
@Table(name = "cultural_activities")
public class CulturalActivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String title;
    
    private String type; // "Movie", "Museum", "Concert", "Theater", etc.
    private String date;
    private int durationMinutes;
    private String notes;
    private int rating; // 1-5
    
    // Constructors
    public CulturalActivity() {}
    
    public CulturalActivity(Long userId, String title, String type, String date, int durationMinutes) {
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.date = date;
        this.durationMinutes = durationMinutes;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int minutes) { this.durationMinutes = minutes; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}
