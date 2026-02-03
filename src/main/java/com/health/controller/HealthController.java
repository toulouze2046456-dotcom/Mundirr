package com.health.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Root endpoint controller
 */
@RestController
public class HealthController {
    
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
            "app", "Vellbeing OS",
            "status", "running",
            "message", "API is available at /api/*"
        ));
    }
}
