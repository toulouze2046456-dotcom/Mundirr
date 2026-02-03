package com.health.entity;

import jakarta.persistence.*;

/**
 * Cultural book tracking for reading progress.
 */
@Entity
@Table(name = "cultural_books")
public class CulturalBook {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String title;
    
    private String author;
    private int progress; // 0-100
    private String category;
    private String status; // "Reading", "Completed", "Want to Read"
    
    // Constructors
    public CulturalBook() {}
    
    public CulturalBook(Long userId, String title, String author, int progress, String category) {
        this.userId = userId;
        this.title = title;
        this.author = author;
        this.progress = progress;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
