package com.health.controller;

import com.health.entity.SubstanceLog;
import com.health.entity.User;
import com.health.repository.SubstanceLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for Substance tracking (cigarettes, etc.)
 */
@RestController
@RequestMapping("/api/substances")
public class SubstanceController extends BaseController {
    
    @Autowired
    private SubstanceLogRepository substanceRepo;
    
    /**
     * Get all substance logs for the user
     */
    @GetMapping
    public List<SubstanceLog> getAll(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return substanceRepo.findByUserIdOrderByDateDesc(user.getId());
    }
    
    /**
     * Get today's substance count
     */
    @GetMapping("/today")
    public Map<String, Object> getToday(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        String today = LocalDate.now().toString();
        
        Optional<SubstanceLog> log = substanceRepo.findByUserIdAndDateAndType(user.getId(), today, "Cigarettes");
        int count = log.map(SubstanceLog::getCount).orElse(0);
        
        return Map.of("count", count, "date", today, "type", "Cigarettes");
    }
    
    /**
     * Add or update a substance log
     */
    @PostMapping
    public SubstanceLog addOrUpdate(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        String type = (String) body.getOrDefault("type", "Cigarettes");
        int count = body.get("count") instanceof Number ? ((Number) body.get("count")).intValue() : 0;
        String date = (String) body.getOrDefault("date", LocalDate.now().toString());
        
        Optional<SubstanceLog> existing = substanceRepo.findByUserIdAndDateAndType(user.getId(), date, type);
        
        if (existing.isPresent()) {
            SubstanceLog log = existing.get();
            log.setCount(count);
            return substanceRepo.save(log);
        }
        
        return substanceRepo.save(new SubstanceLog(user.getId(), type, count, date));
    }
    
    /**
     * Increment substance count for today
     */
    @PostMapping("/increment")
    public SubstanceLog increment(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        String type = (String) body.getOrDefault("type", "Cigarettes");
        int amount = body.get("amount") instanceof Number ? ((Number) body.get("amount")).intValue() : 1;
        String today = LocalDate.now().toString();
        
        Optional<SubstanceLog> existing = substanceRepo.findByUserIdAndDateAndType(user.getId(), today, type);
        
        if (existing.isPresent()) {
            SubstanceLog log = existing.get();
            log.setCount(log.getCount() + amount);
            return substanceRepo.save(log);
        }
        
        return substanceRepo.save(new SubstanceLog(user.getId(), type, amount, today));
    }
    
    /**
     * Delete a substance log
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable Long id) {
        User user = getAuthenticatedUser(request);
        
        return substanceRepo.findById(id)
            .filter(log -> log.getUserId().equals(user.getId()))
            .map(log -> {
                substanceRepo.delete(log);
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
        
        List<SubstanceLog> logs = substanceRepo.findByUserIdAndDateGreaterThanEqual(user.getId(), startDate);
        
        int total = logs.stream().mapToInt(SubstanceLog::getCount).sum();
        double average = logs.isEmpty() ? 0 : (double) total / days;
        
        return Map.of(
            "total", total,
            "average", Math.round(average * 10) / 10.0,
            "days", days,
            "logs", logs
        );
    }
}
