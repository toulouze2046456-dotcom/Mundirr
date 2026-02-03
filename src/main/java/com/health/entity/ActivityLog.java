package com.health.entity;

import jakarta.persistence.*;

/**
 * Activity log for tracking physical workouts and nutrition entries.
 */
@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String type; // "PHYSICAL" or "NUTRITION"
    
    private String date;
    private int value;    // Minutes (Physical) or Calories (Nutrition)
    private int protein;  // Protein grams (Nutrition only)
    private String label;
    private String exerciseId; // For linking to Wger API exercises
    private String muscleGroups; // Comma-separated muscle groups
    
    // Constructors
    public ActivityLog() {}
    
    public ActivityLog(Long userId, String type, String date, int value, int protein, String label) {
        this.userId = userId;
        this.type = type;
        this.date = date;
        this.value = value;
        this.protein = protein;
        this.label = label;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    
    public int getProtein() { return protein; }
    public void setProtein(int protein) { this.protein = protein; }
    
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    
    public String getExerciseId() { return exerciseId; }
    public void setExerciseId(String exerciseId) { this.exerciseId = exerciseId; }
    
    public String getMuscleGroups() { return muscleGroups; }
    public void setMuscleGroups(String muscleGroups) { this.muscleGroups = muscleGroups; }
}
