package com.health.entity;

import jakarta.persistence.*;

/**
 * Historical market data for financial tracking.
 * Stores daily closing prices for various market symbols (stocks, ETFs, crypto).
 */
@Entity
@Table(name = "market_history", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"symbol", "date"}))
public class MarketHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String symbol; // BTC, NASDAQ, S&P500, NVDA, PLTR, WAR, GOLD, OIL
    
    @Column(nullable = false)
    private String date; // ISO date format: YYYY-MM-DD
    
    @Column(nullable = false)
    private double price;
    
    // Constructors
    public MarketHistory() {}
    
    public MarketHistory(String symbol, String date, double price) {
        this.symbol = symbol;
        this.date = date;
        this.price = price;
    }
    
    // Getters and Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getSymbol() { 
        return symbol; 
    }
    
    public void setSymbol(String symbol) { 
        this.symbol = symbol; 
    }
    
    public String getDate() { 
        return date; 
    }
    
    public void setDate(String date) { 
        this.date = date; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    public void setPrice(double price) { 
        this.price = price; 
    }
}
