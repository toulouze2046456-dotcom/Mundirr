package com.health.entity;

import jakarta.persistence.*;

/**
 * Alcohol tracking log.
 */
@Entity
@Table(name = "alcohol_logs")
public class AlcoholLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    private int units;
    
    @Column(nullable = false)
    private String date;
    
    private String drinkType; // "Beer", "Wine", "Spirits", etc.
    
    // Constructors
    public AlcoholLog() {}
    
    public AlcoholLog(Long userId, int units, String date) {
        this.userId = userId;
        this.units = units;
        this.date = date;
    }
    
    public AlcoholLog(Long userId, int units, String date, String drinkType) {
        this.userId = userId;
        this.units = units;
        this.date = date;
        this.drinkType = drinkType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getDrinkType() { return drinkType; }
    public void setDrinkType(String drinkType) { this.drinkType = drinkType; }
}
