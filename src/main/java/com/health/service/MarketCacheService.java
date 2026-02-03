package com.health.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class MarketCacheService {
    // Cache for current market prices (/markets endpoint)
    private Map<String, Object> cachedData = null;
    private Instant lastUpdate = Instant.EPOCH;
    
    // Cache for historical market data (/markets/history endpoint)
    private Map<String, Map<String, Double>> cachedHistoryData = null;
    private Instant lastHistoryUpdate = Instant.EPOCH;

    public synchronized Map<String, Object> getCachedData() {
        if (cachedData == null) return null;
        // If more than 23h 59m passed, invalidate
        if (Instant.now().isAfter(lastUpdate.plusSeconds(24 * 60 * 60 - 60))) {
            cachedData = null;
            return null;
        }
        return cachedData;
    }

    public synchronized void setCachedData(Map<String, Object> data) {
        this.cachedData = data;
        this.lastUpdate = Instant.now();
    }
    
    // History cache methods - cached for 24 hours
    public synchronized Map<String, Map<String, Double>> getCachedHistoryData() {
        if (cachedHistoryData == null) return null;
        // If more than 23h 59m passed, invalidate
        if (Instant.now().isAfter(lastHistoryUpdate.plusSeconds(24 * 60 * 60 - 60))) {
            cachedHistoryData = null;
            return null;
        }
        return cachedHistoryData;
    }

    public synchronized void setCachedHistoryData(Map<String, Map<String, Double>> data) {
        this.cachedHistoryData = data;
        this.lastHistoryUpdate = Instant.now();
    }
}