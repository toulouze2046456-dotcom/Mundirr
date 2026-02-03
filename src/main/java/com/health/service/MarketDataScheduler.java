package com.health.service;

import com.health.entity.MarketHistory;
import com.health.repository.MarketHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.Instant;
import java.util.*;

/**
 * Scheduled service that fetches market data ONCE per day from Alpha Vantage API
 * and stores it in the database. All users share this single daily fetch.
 * 
 * This minimizes API calls while providing all users with up-to-date market data.
 */
@Service
public class MarketDataScheduler {
    
    @Autowired
    private MarketHistoryRepository marketHistoryRepo;
    
    @Autowired
    private MarketCacheService marketCacheService;
    
    @Value("${alphavantage.api.key:}")
    private String alphaVantageApiKey;
    
    private static final String ALPHA_VANTAGE_BASE_URL = "https://www.alphavantage.co/query";
    private final RestTemplate restTemplate = new RestTemplate();
    
    // Symbol mappings: [API Symbol, Storage Key, Display Name]
    private static final String[][] SYMBOLS = {
        {"QQQ", "NASDAQ", "NASDAQ (QQQ ETF)"},
        {"SPY", "S&P500", "S&P 500 (SPY ETF)"},
        {"NVDA", "NVDA", "NVIDIA"},
        {"PLTR", "PLTR", "Palantir"},
        {"ITA", "WAR", "War Index (ITA ETF)"},
        {"GLD", "GOLD", "Gold (GLD ETF)"},
        {"USO", "OIL", "Oil (USO ETF)"}
    };
    
    /**
     * On application startup, check if we have today's data.
     * If not, fetch it immediately.
     */
    @PostConstruct
    public void onStartup() {
        System.out.println("=== MarketDataScheduler: Checking market data on startup ===");
        String today = LocalDate.now().toString();
        
        // Check if we have today's data for all symbols
        boolean hasTodayData = true;
        String[] allSymbols = {"BTC", "NASDAQ", "S&P500", "NVDA", "PLTR", "WAR", "GOLD", "OIL"};
        for (String symbol : allSymbols) {
            if (!marketHistoryRepo.existsBySymbolAndDate(symbol, today)) {
                hasTodayData = false;
                break;
            }
        }
        
        if (!hasTodayData) {
            System.out.println("Missing today's market data - fetching now...");
            fetchAndStoreDailyData();
        } else {
            System.out.println("Today's market data already exists in database.");
            // Still refresh cache from database
            refreshCacheFromDatabase();
        }
    }
    
    /**
     * Run at 6:00 AM every day to fetch fresh market data.
     * Alpha Vantage provides end-of-day data, so morning fetch gets yesterday's close.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void scheduledDailyFetch() {
        System.out.println("=== Scheduled daily market data fetch ===");
        fetchAndStoreDailyData();
    }
    
    /**
     * Also run at market close (4:30 PM EST / 22:30 CET) to get today's data
     */
    @Scheduled(cron = "0 30 22 * * MON-FRI")
    public void scheduledMarketCloseFetch() {
        System.out.println("=== Market close fetch ===");
        fetchAndStoreDailyData();
    }
    
    /**
     * Fetch current market data from Alpha Vantage and store in database.
     * This is the SINGLE DAILY FETCH that serves all users.
     */
    public synchronized void fetchAndStoreDailyData() {
        if (alphaVantageApiKey == null || alphaVantageApiKey.isEmpty()) {
            System.err.println("Alpha Vantage API key not configured!");
            return;
        }
        
        String today = LocalDate.now().toString();
        List<Map<String, Object>> currentPrices = new ArrayList<>();
        
        System.out.println("Fetching market data for " + today + "...");
        
        // Fetch Bitcoin
        try {
            String btcUrl = ALPHA_VANTAGE_BASE_URL + "?function=CURRENCY_EXCHANGE_RATE&from_currency=BTC&to_currency=USD&apikey=" + alphaVantageApiKey;
            @SuppressWarnings("unchecked")
            Map<String, Object> btcResponse = restTemplate.getForObject(btcUrl, Map.class);
            
            if (btcResponse != null && btcResponse.containsKey("Realtime Currency Exchange Rate")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> rate = (Map<String, Object>) btcResponse.get("Realtime Currency Exchange Rate");
                double price = Double.parseDouble(rate.get("5. Exchange Rate").toString());
                
                // Save to database
                saveToDatabase("BTC", today, price);
                
                // Add to current prices cache
                Map<String, Object> btc = new HashMap<>();
                btc.put("symbol", "BTC");
                btc.put("name", "Bitcoin");
                btc.put("price", price);
                btc.put("lastUpdate", Instant.now().toString());
                currentPrices.add(btc);
                
                System.out.println("BTC: $" + price);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch BTC: " + e.getMessage());
        }
        
        // Fetch stocks/ETFs
        for (String[] symbolInfo : SYMBOLS) {
            String apiSymbol = symbolInfo[0];
            String storageKey = symbolInfo[1];
            String displayName = symbolInfo[2];
            
            try {
                // Small delay to avoid rate limiting (5 calls/minute on free tier)
                Thread.sleep(12500); // 12.5 seconds between calls = ~5 calls/min
                
                String url = ALPHA_VANTAGE_BASE_URL + "?function=GLOBAL_QUOTE&symbol=" + apiSymbol + "&apikey=" + alphaVantageApiKey;
                @SuppressWarnings("unchecked")
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                
                if (response != null && response.containsKey("Global Quote")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> quote = (Map<String, Object>) response.get("Global Quote");
                    
                    if (quote != null && quote.get("05. price") != null) {
                        double price = Double.parseDouble(quote.get("05. price").toString());
                        double change = quote.get("09. change") != null ? Double.parseDouble(quote.get("09. change").toString()) : 0;
                        double changePercent = 0;
                        if (quote.get("10. change percent") != null) {
                            changePercent = Double.parseDouble(quote.get("10. change percent").toString().replace("%", ""));
                        }
                        
                        // Save to database
                        saveToDatabase(storageKey, today, price);
                        
                        // Add to current prices cache
                        Map<String, Object> entry = new HashMap<>();
                        entry.put("symbol", storageKey);
                        entry.put("name", displayName);
                        entry.put("price", price);
                        entry.put("change24h", Math.round(change * 100) / 100.0);
                        entry.put("changePercent", Math.round(changePercent * 100) / 100.0);
                        entry.put("lastUpdate", Instant.now().toString());
                        currentPrices.add(entry);
                        
                        System.out.println(storageKey + ": $" + price + " (" + (changePercent >= 0 ? "+" : "") + changePercent + "%)");
                    }
                } else if (response != null && response.containsKey("Note")) {
                    // Rate limit hit
                    System.err.println("API rate limit hit. Will retry later.");
                    break;
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch " + apiSymbol + ": " + e.getMessage());
            }
        }
        
        // Update cache with fresh data
        if (!currentPrices.isEmpty()) {
            Map<String, Object> cacheObj = new HashMap<>();
            cacheObj.put("results", currentPrices);
            marketCacheService.setCachedData(cacheObj);
            System.out.println("Updated market cache with " + currentPrices.size() + " symbols.");
        }
    }
    
    /**
     * Fetch historical data for past 30 days (one-time backfill).
     * Should only be called once when database is empty.
     */
    public void fetchHistoricalData(int days) {
        System.out.println("=== Fetching " + days + " days of historical data ===");
        
        // Fetch BTC historical
        try {
            String btcUrl = ALPHA_VANTAGE_BASE_URL + "?function=DIGITAL_CURRENCY_DAILY&symbol=BTC&market=USD&apikey=" + alphaVantageApiKey;
            @SuppressWarnings("unchecked")
            Map<String, Object> btcResponse = restTemplate.getForObject(btcUrl, Map.class);
            
            if (btcResponse != null && btcResponse.containsKey("Time Series (Digital Currency Daily)")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> timeSeries = (Map<String, Object>) btcResponse.get("Time Series (Digital Currency Daily)");
                int count = 0;
                for (Map.Entry<String, Object> entry : timeSeries.entrySet()) {
                    if (count >= days) break;
                    String date = entry.getKey();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> dayData = (Map<String, Object>) entry.getValue();
                    Object closeObj = dayData.get("4a. close (USD)");
                    if (closeObj == null) closeObj = dayData.get("4. close");
                    if (closeObj != null) {
                        double price = Double.parseDouble(closeObj.toString());
                        saveToDatabase("BTC", date, price);
                        count++;
                    }
                }
                System.out.println("Saved " + count + " days of BTC history");
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch BTC history: " + e.getMessage());
        }
        
        // Fetch stock/ETF historical
        for (String[] symbolInfo : SYMBOLS) {
            String apiSymbol = symbolInfo[0];
            String storageKey = symbolInfo[1];
            
            try {
                Thread.sleep(12500); // Respect rate limit
                
                String url = ALPHA_VANTAGE_BASE_URL + "?function=TIME_SERIES_DAILY&symbol=" + apiSymbol + "&outputsize=compact&apikey=" + alphaVantageApiKey;
                @SuppressWarnings("unchecked")
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                
                if (response != null && response.containsKey("Time Series (Daily)")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> timeSeries = (Map<String, Object>) response.get("Time Series (Daily)");
                    int count = 0;
                    for (Map.Entry<String, Object> entry : timeSeries.entrySet()) {
                        if (count >= days) break;
                        String date = entry.getKey();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> dayData = (Map<String, Object>) entry.getValue();
                        double price = Double.parseDouble(dayData.get("4. close").toString());
                        saveToDatabase(storageKey, date, price);
                        count++;
                    }
                    System.out.println("Saved " + count + " days of " + storageKey + " history");
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch history for " + apiSymbol + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Save a price to the database if it doesn't already exist.
     */
    private void saveToDatabase(String symbol, String date, double price) {
        if (!marketHistoryRepo.existsBySymbolAndDate(symbol, date)) {
            marketHistoryRepo.save(new MarketHistory(symbol, date, price));
        }
    }
    
    /**
     * Refresh the in-memory cache from database.
     */
    private void refreshCacheFromDatabase() {
        String today = LocalDate.now().toString();
        List<MarketHistory> todayData = marketHistoryRepo.findByDate(today);
        
        if (todayData.isEmpty()) {
            // Get most recent data
            String[] symbols = {"BTC", "NASDAQ", "S&P500", "NVDA", "PLTR", "WAR", "GOLD", "OIL"};
            String[] names = {"Bitcoin", "NASDAQ (QQQ ETF)", "S&P 500 (SPY ETF)", "NVIDIA", "Palantir", "War Index (ITA ETF)", "Gold (GLD ETF)", "Oil (USO ETF)"};
            
            List<Map<String, Object>> results = new ArrayList<>();
            for (int i = 0; i < symbols.length; i++) {
                String latestDate = marketHistoryRepo.findLatestDateForSymbol(symbols[i]);
                if (latestDate != null) {
                    Optional<MarketHistory> record = marketHistoryRepo.findBySymbolAndDate(symbols[i], latestDate);
                    if (record.isPresent()) {
                        Map<String, Object> entry = new HashMap<>();
                        entry.put("symbol", symbols[i]);
                        entry.put("name", names[i]);
                        entry.put("price", record.get().getPrice());
                        entry.put("lastUpdate", latestDate);
                        results.add(entry);
                    }
                }
            }
            
            if (!results.isEmpty()) {
                Map<String, Object> cacheObj = new HashMap<>();
                cacheObj.put("results", results);
                marketCacheService.setCachedData(cacheObj);
            }
        } else {
            List<Map<String, Object>> results = new ArrayList<>();
            String[] names = {"Bitcoin", "NASDAQ (QQQ ETF)", "S&P 500 (SPY ETF)", "NVIDIA", "Palantir", "War Index (ITA ETF)", "Gold (GLD ETF)", "Oil (USO ETF)"};
            Map<String, String> symbolToName = Map.of(
                "BTC", "Bitcoin", "NASDAQ", "NASDAQ (QQQ ETF)", "S&P500", "S&P 500 (SPY ETF)",
                "NVDA", "NVIDIA", "PLTR", "Palantir", "WAR", "War Index (ITA ETF)",
                "GOLD", "Gold (GLD ETF)", "OIL", "Oil (USO ETF)"
            );
            
            for (MarketHistory record : todayData) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("symbol", record.getSymbol());
                entry.put("name", symbolToName.getOrDefault(record.getSymbol(), record.getSymbol()));
                entry.put("price", record.getPrice());
                entry.put("lastUpdate", today);
                results.add(entry);
            }
            
            Map<String, Object> cacheObj = new HashMap<>();
            cacheObj.put("results", results);
            marketCacheService.setCachedData(cacheObj);
        }
    }
}
