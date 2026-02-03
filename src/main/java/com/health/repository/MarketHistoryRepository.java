package com.health.repository;

import com.health.entity.MarketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketHistoryRepository extends JpaRepository<MarketHistory, Long> {
    // Find specific symbol on specific date
    Optional<MarketHistory> findBySymbolAndDate(String symbol, String date);
    
    // Find all data for a symbol
    List<MarketHistory> findBySymbol(String symbol);
    
    // Find all data for a specific date
    List<MarketHistory> findByDate(String date);
    
    // Find data within date range for a symbol
    List<MarketHistory> findBySymbolAndDateBetweenOrderByDateDesc(String symbol, String startDate, String endDate);
    
    // Get the most recent date for a symbol
    @Query("SELECT MAX(m.date) FROM MarketHistory m WHERE m.symbol = ?1")
    String findLatestDateForSymbol(String symbol);
    
    // Check if data exists for symbol and date
    boolean existsBySymbolAndDate(String symbol, String date);
}
