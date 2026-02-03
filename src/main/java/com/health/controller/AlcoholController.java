package com.health.controller;

import com.health.entity.AlcoholLog;
import com.health.entity.User;
import com.health.repository.AlcoholLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for Alcohol tracking.
 */
@RestController
@RequestMapping("/api/alcohol")
public class AlcoholController extends BaseController {
    
    @Autowired
    private AlcoholLogRepository alcoholRepo;
    
    /**
     * Get all alcohol logs for the user
     */
    @GetMapping
    public List<AlcoholLog> getAll(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return alcoholRepo.findByUserIdOrderByDateDesc(user.getId());
    }
    
    /**
     * Get today's alcohol units
     */
    @GetMapping("/today")
    public Map<String, Object> getToday(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        String today = LocalDate.now().toString();
        
        Optional<AlcoholLog> log = alcoholRepo.findByUserIdAndDate(user.getId(), today);
        int units = log.map(AlcoholLog::getUnits).orElse(0);
        
        return Map.of("units", units, "date", today);
    }
    
    /**
     * Add or update alcohol log
     */
    @PostMapping
    public AlcoholLog addOrUpdate(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        int units = body.get("units") instanceof Number ? ((Number) body.get("units")).intValue() : 0;
        String date = (String) body.getOrDefault("date", LocalDate.now().toString());
        String drinkType = (String) body.get("drinkType");
        
        Optional<AlcoholLog> existing = alcoholRepo.findByUserIdAndDate(user.getId(), date);
        
        if (existing.isPresent()) {
            AlcoholLog log = existing.get();
            log.setUnits(units);
            if (drinkType != null) log.setDrinkType(drinkType);
            return alcoholRepo.save(log);
        }
        
        AlcoholLog log = new AlcoholLog(user.getId(), units, date);
        if (drinkType != null) log.setDrinkType(drinkType);
        return alcoholRepo.save(log);
    }
    
    /**
     * Increment alcohol units for today
     */
    @PostMapping("/increment")
    public AlcoholLog increment(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        int amount = body.get("amount") instanceof Number ? ((Number) body.get("amount")).intValue() : 1;
        String today = LocalDate.now().toString();
        
        Optional<AlcoholLog> existing = alcoholRepo.findByUserIdAndDate(user.getId(), today);
        
        if (existing.isPresent()) {
            AlcoholLog log = existing.get();
            log.setUnits(log.getUnits() + amount);
            return alcoholRepo.save(log);
        }
        
        return alcoholRepo.save(new AlcoholLog(user.getId(), amount, today));
    }
    
    /**
     * Delete an alcohol log
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable Long id) {
        User user = getAuthenticatedUser(request);
        
        return alcoholRepo.findById(id)
            .filter(log -> log.getUserId().equals(user.getId()))
            .map(log -> {
                alcoholRepo.delete(log);
                return ResponseEntity.ok(Map.of("success", true));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get stats for the last N days
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats(HttpServletRequest request, @RequestParam(defaultValue = "7") int days) {
        User user = getAuthenticatedUser(request);
        String startDate = LocalDate.now().minusDays(days).toString();
        
        List<AlcoholLog> logs = alcoholRepo.findByUserIdAndDateGreaterThanEqual(user.getId(), startDate);
        
        int total = logs.stream().mapToInt(AlcoholLog::getUnits).sum();
        double average = logs.isEmpty() ? 0 : (double) total / days;
        
        // WHO recommends max 14 units per week
        int weeklyRecommended = 14;
        String assessment = total > weeklyRecommended ? "High" : total > 7 ? "Moderate" : "Low";
        
        return Map.of(
            "total", total,
            "average", Math.round(average * 10) / 10.0,
            "days", days,
            "weeklyLimit", weeklyRecommended,
            "assessment", assessment,
            "logs", logs
        );
    }
}
