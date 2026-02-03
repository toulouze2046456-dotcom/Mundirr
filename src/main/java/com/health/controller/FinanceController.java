package com.health.controller;

import com.health.entity.FinanceTransaction;
import com.health.entity.MarketHistory;
import com.health.entity.User;
import com.health.repository.FinanceTransactionRepository;
import com.health.repository.MarketHistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.health.service.MarketCacheService;

import java.time.LocalDate;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for Financial health tracking.
 * Uses Alpha Vantage API for real-time market data with database persistence.
 */
@RestController
@RequestMapping("/api/finance")
public class FinanceController extends BaseController {
    @Autowired
    private MarketCacheService marketCacheService;
    
    @Autowired
    private FinanceTransactionRepository financeRepo;
    
    @Autowired
    private MarketHistoryRepository marketHistoryRepo;
    
    @Value("${alphavantage.api.key:}")
    private String alphaVantageApiKey;
    
    private static final String ALPHA_VANTAGE_BASE_URL = "https://www.alphavantage.co/query";
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Autowired
    private com.health.service.MarketDataScheduler marketDataScheduler;
    
    /**
     * Get current market data.
     * Primary source: in-memory cache (refreshed daily by scheduler)
     * Fallback: database (historical data)
     * 
     * The MarketDataScheduler handles all API calls - once per day for all users.
     */
    @GetMapping("/markets")
    public ResponseEntity<?> getMarketData() {
        // Try cache first (populated by scheduler)
        Map<String, Object> cached = marketCacheService.getCachedData();
        if (cached != null && cached.get("results") != null) {
            return ResponseEntity.ok(cached.get("results"));
        }

        // Fallback to database - get latest data for each symbol
        List<Map<String, Object>> results = getLatestDataFromDatabase();
        
        if (!results.isEmpty()) {
            // Update cache with database data
            Map<String, Object> cacheObj = new HashMap<>();
            cacheObj.put("results", results);
            marketCacheService.setCachedData(cacheObj);
            return ResponseEntity.ok(results);
        }
        
        // No data at all - trigger a fetch (first-time setup)
        System.out.println("No market data in cache or database - triggering initial fetch...");
        marketDataScheduler.fetchAndStoreDailyData();
        
        // Try cache again after fetch
        cached = marketCacheService.getCachedData();
        if (cached != null && cached.get("results") != null) {
            return ResponseEntity.ok(cached.get("results"));
        }
        
        // Return empty with message
        return ResponseEntity.ok(Map.of(
            "error", true, 
            "message", "Market data fetch in progress. Please refresh in a minute."
        ));
    }
    
    /**
     * Get historical market data from database.
     * All data comes from the database - the scheduler populates it daily.
     * Format: { "2026-01-25": { "BTC": 87000, "NASDAQ": 520, "S&P500": 600, ... }, ... }
     */
    @GetMapping("/markets/history")
    public ResponseEntity<?> getMarketHistory(@RequestParam(defaultValue = "30") int days) {
        System.out.println("Fetching market history for " + days + " days from database...");
        
        // Load all data from database
        Map<String, Map<String, Double>> result = new HashMap<>();
        List<MarketHistory> allData = marketHistoryRepo.findAll();
        
        for (MarketHistory record : allData) {
            result.computeIfAbsent(record.getDate(), k -> new HashMap<>());
            result.get(record.getDate()).put(record.getSymbol(), record.getPrice());
        }
        
        System.out.println("Loaded " + allData.size() + " records from database (" + result.size() + " unique dates)");
        
        // If we have no data, trigger historical fetch
        if (result.isEmpty()) {
            System.out.println("No historical data - triggering backfill...");
            marketDataScheduler.fetchHistoricalData(days);
            
            // Reload from database
            allData = marketHistoryRepo.findAll();
            for (MarketHistory record : allData) {
                result.computeIfAbsent(record.getDate(), k -> new HashMap<>());
                result.get(record.getDate()).put(record.getSymbol(), record.getPrice());
            }
        }
        
        // Filter to requested number of days
        LocalDate today = LocalDate.now();
        Map<String, Map<String, Double>> filteredResult = new HashMap<>();
        for (int i = 0; i < days; i++) {
            String date = today.minusDays(i).toString();
            if (result.containsKey(date)) {
                filteredResult.put(date, result.get(date));
            }
        }
        
        System.out.println("Returning " + filteredResult.size() + " days of market history");
        return ResponseEntity.ok(filteredResult.isEmpty() ? result : filteredResult);
    }
    
    /**
     * Admin endpoint to manually trigger market data fetch.
     * Useful for testing or forcing a refresh.
     */
    @PostMapping("/markets/refresh")
    public ResponseEntity<?> refreshMarketData() {
        System.out.println("Manual market data refresh triggered...");
        marketDataScheduler.fetchAndStoreDailyData();
        return ResponseEntity.ok(Map.of("success", true, "message", "Market data refresh triggered"));
    }
    
    /**
     * Admin endpoint to backfill historical data.
     */
    @PostMapping("/markets/backfill")
    public ResponseEntity<?> backfillMarketData(@RequestParam(defaultValue = "30") int days) {
        System.out.println("Backfilling " + days + " days of market data...");
        marketDataScheduler.fetchHistoricalData(days);
        return ResponseEntity.ok(Map.of("success", true, "message", "Backfill of " + days + " days triggered"));
    }
    
    /**
     * Get all transactions for the user
     */
    @GetMapping("/transactions")
    public List<FinanceTransaction> getAll(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return financeRepo.findByUserIdOrderByDateDesc(user.getId());
    }
    
    /**
     * Add a transaction
     */
    @PostMapping("/transaction")
    public FinanceTransaction addTransaction(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        String description = (String) body.getOrDefault("description", "Transaction");
        double amount = body.get("amount") instanceof Number 
            ? ((Number) body.get("amount")).doubleValue() : 0;
        String type = (String) body.getOrDefault("type", "EXPENSE");
        String category = (String) body.getOrDefault("category", "Other");
        String date = (String) body.getOrDefault("date", LocalDate.now().toString());
        
        return financeRepo.save(new FinanceTransaction(
            user.getId(), description, amount, type, category, date
        ));
    }
    
    /**
     * Delete a transaction
     */
    @DeleteMapping("/transaction/{id}")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable Long id) {
        User user = getAuthenticatedUser(request);
        
        return financeRepo.findById(id)
            .filter(txn -> txn.getUserId().equals(user.getId()))
            .map(txn -> {
                financeRepo.delete(txn);
                return ResponseEntity.ok(Map.of("success", true));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get financial stats and rankings
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        List<FinanceTransaction> txns = financeRepo.findByUserId(user.getId());
        
        double income = txns.stream()
            .filter(t -> "INCOME".equals(t.getType()))
            .mapToDouble(FinanceTransaction::getAmount)
            .sum();
        
        double expenses = txns.stream()
            .filter(t -> "EXPENSE".equals(t.getType()))
            .mapToDouble(FinanceTransaction::getAmount)
            .sum();
        
        double balance = income - expenses;
        double savingsRate = income > 0 ? ((income - expenses) / income) * 100 : 0;
        
        // Calculate ranking score based on savings rate
        // 50% savings = 100 score, 0% = 50 score, negative = below 50
        double rankingScore = Math.max(0, Math.min(100, 50 + savingsRate * 1.0));
        
        // Breakdown by category
        Map<String, Double> categoryBreakdown = txns.stream()
            .filter(t -> "EXPENSE".equals(t.getType()))
            .collect(Collectors.groupingBy(
                FinanceTransaction::getCategory,
                Collectors.summingDouble(FinanceTransaction::getAmount)
            ));
        
        Map<String, Object> result = new HashMap<>();
        result.put("balance", Math.round(balance * 100) / 100.0);
        result.put("income", Math.round(income * 100) / 100.0);
        result.put("expenses", Math.round(expenses * 100) / 100.0);
        result.put("savingsRate", Math.round(savingsRate));
        result.put("rankingScore", Math.round(rankingScore));
        result.put("categoryBreakdown", categoryBreakdown);
        result.put("transactionCount", txns.size());
        
        return result;
    }
    
    /**
     * Get monthly breakdown
     */
    @GetMapping("/monthly")
    public Map<String, Object> getMonthly(HttpServletRequest request, 
                                          @RequestParam(required = false) String month) {
        User user = getAuthenticatedUser(request);
        
        String targetMonth = month != null ? month : LocalDate.now().toString().substring(0, 7);
        
        List<FinanceTransaction> txns = financeRepo.findByUserId(user.getId()).stream()
            .filter(t -> t.getDate().startsWith(targetMonth))
            .toList();
        
        double income = txns.stream()
            .filter(t -> "INCOME".equals(t.getType()))
            .mapToDouble(FinanceTransaction::getAmount)
            .sum();
        
        double expenses = txns.stream()
            .filter(t -> "EXPENSE".equals(t.getType()))
            .mapToDouble(FinanceTransaction::getAmount)
            .sum();
        
        return Map.of(
            "month", targetMonth,
            "income", income,
            "expenses", expenses,
            "net", income - expenses,
            "transactions", txns
        );
    }
    
    /**
     * Get latest market data from database as fallback when API fails.
     */
    private List<Map<String, Object>> getLatestDataFromDatabase() {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String[][] symbols = {
            {"BTC", "Bitcoin"},
            {"NASDAQ", "NASDAQ (QQQ ETF)"},
            {"S&P500", "S&P 500 (SPY ETF)"},
            {"NVDA", "NVIDIA"},
            {"PLTR", "Palantir"},
            {"WAR", "War Index (ITA ETF)"},
            {"GOLD", "Gold (GLD ETF)"},
            {"OIL", "Oil (USO ETF)"}
        };
        
        for (String[] symbolInfo : symbols) {
            String symbol = symbolInfo[0];
            String name = symbolInfo[1];
            
            // Get the latest price for this symbol from database
            String latestDate = marketHistoryRepo.findLatestDateForSymbol(symbol);
            if (latestDate != null) {
                Optional<MarketHistory> recordOpt = marketHistoryRepo.findBySymbolAndDate(symbol, latestDate);
                if (recordOpt.isPresent()) {
                    MarketHistory record = recordOpt.get();
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("symbol", symbol);
                    entry.put("name", name);
                    entry.put("price", record.getPrice());
                    entry.put("lastUpdate", record.getDate() + " (cached)");
                    results.add(entry);
                }
            }
        }
        
        return results;
    }
    

}
