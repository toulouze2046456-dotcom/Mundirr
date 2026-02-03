package com.health.entity;

import jakarta.persistence.*;

/**
 * Financial transaction tracking.
 */
@Entity
@Table(name = "finance_transactions")
public class FinanceTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    private String description;
    private double amount;
    private String type; // "INCOME" or "EXPENSE"
    private String category;
    
    @Column(nullable = false)
    private String date;
    
    // Constructors
    public FinanceTransaction() {}
    
    public FinanceTransaction(Long userId, String description, double amount, 
                              String type, String category, String date) {
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
