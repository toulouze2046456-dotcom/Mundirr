package com.health.entity;

import jakarta.persistence.*;

/**
 * Supplement entity for user's supplement stack.
 */
@Entity
@Table(name = "supplements")
public class Supplement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String name;
    
    private String dosage;
    private String type; // "Supplement", "Protein", "Drug"
    private String frequency; // "daily", "weekly", etc.
    private String timeOfDay; // "morning", "evening", etc.
    private String notes;
    
    // Constructors
    public Supplement() {}
    
    public Supplement(Long userId, String name, String dosage, String type) {
        this.userId = userId;
        this.name = name;
        this.dosage = dosage;
        this.type = type;
    }
    
    public Supplement(Long userId, String name, String dosage, String type, String frequency, String timeOfDay, String notes) {
        this.userId = userId;
        this.name = name;
        this.dosage = dosage;
        this.type = type;
        this.frequency = frequency;
        this.timeOfDay = timeOfDay;
        this.notes = notes;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    
    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
