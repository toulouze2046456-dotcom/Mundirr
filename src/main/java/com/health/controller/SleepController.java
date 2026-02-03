package com.health.controller;

import com.health.entity.SleepLog;
import com.health.entity.User;
import com.health.repository.SleepLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for Sleep tracking with scientific feedback.
 */
@RestController
@RequestMapping("/api/sleep")
public class SleepController extends BaseController {
    
    @Autowired
    private SleepLogRepository sleepRepo;
    
    /**
     * Get all sleep logs for the user (newest first)
     */
    @GetMapping
    public List<SleepLog> getAll(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return sleepRepo.findByUserIdOrderByDateDesc(user.getId());
    }
    
    /**
     * Get the latest sleep entry
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatest(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return sleepRepo.findFirstByUserIdOrderByDateDesc(user.getId())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.noContent().build());
    }
    
    /**
     * Add a sleep log with scientific assessment
     */
    @PostMapping
    public SleepLog addSleep(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        double hours = body.get("hours") instanceof Number 
            ? ((Number) body.get("hours")).doubleValue() : 7.0;
        String quality = (String) body.getOrDefault("quality", "Good");
        String date = (String) body.getOrDefault("date", LocalDate.now().toString());
        
        // Calculate sleep score based on duration and quality
        double qualityMultiplier = switch (quality) {
            case "Excellent" -> 1.0;
            case "Good" -> 0.9;
            case "Fair" -> 0.75;
            default -> 0.6; // Poor
        };
        
        // Optimal sleep is 7-9 hours, penalty for deviation
        double deviation = Math.abs(hours - 8.0) * 10;
        int score = (int) Math.max(0, Math.min(100, (100 - deviation) * qualityMultiplier));
        
        // Generate scientific feedback based on hours
        String consequenceType;
        String scientificFeedback;
        
        if (hours < 5) {
            consequenceType = "CRITICAL DEPRIVATION";
            scientificFeedback = "Severe cognitive impairment expected. Studies show <5h sleep increases " +
                "cortisol by 37%, reduces testosterone by 10-15%, and impairs immune function by 70%. " +
                "Reaction time equivalent to 0.1% BAC.";
        } else if (hours < 6) {
            consequenceType = "SEVERE DEPRIVATION";
            scientificFeedback = "Significant hormonal disruption. Growth hormone secretion reduced by 50%. " +
                "Insulin sensitivity decreases, increasing diabetes risk. Memory consolidation severely impaired.";
        } else if (hours < 7) {
            consequenceType = "MILD DEPRIVATION";
            scientificFeedback = "Reduced focus and elevated cortisol expected. Recovery from exercise impaired. " +
                "Risk of weight gain increased due to ghrelin/leptin imbalance. Inflammation markers elevated.";
        } else if (hours <= 9) {
            consequenceType = "OPTIMAL ZONE";
            scientificFeedback = "Peak recovery zone. Testosterone peaks during deep sleep phases. " +
                "Memory consolidation optimal. HGH release maximized. Immune function at full capacity.";
        } else {
            consequenceType = "OVERSLEEPING";
            scientificFeedback = "Possible inflammation markers elevation. Associated with increased mortality " +
                "in studies. May indicate underlying health issues. Circadian rhythm disruption possible.";
        }
        
        return sleepRepo.save(new SleepLog(
            user.getId(), hours, quality, date, score, scientificFeedback, consequenceType
        ));
    }
    
    /**
     * Delete a sleep log
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable Long id) {
        User user = getAuthenticatedUser(request);
        
        return sleepRepo.findById(id)
            .filter(log -> log.getUserId().equals(user.getId()))
            .map(log -> {
                sleepRepo.delete(log);
                return ResponseEntity.ok(Map.of("success", true));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get sleep stats for the last N days
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats(HttpServletRequest request, @RequestParam(defaultValue = "7") int days) {
        User user = getAuthenticatedUser(request);
        String startDate = LocalDate.now().minusDays(days).toString();
        
        List<SleepLog> logs = sleepRepo.findByUserIdAndDateGreaterThanEqual(user.getId(), startDate);
        
        double totalHours = logs.stream().mapToDouble(SleepLog::getHours).sum();
        double avgHours = logs.isEmpty() ? 0 : totalHours / logs.size();
        double avgScore = logs.stream().mapToInt(SleepLog::getScore).average().orElse(0);
        
        // Sleep debt calculation (assuming 8h optimal)
        double sleepDebt = Math.max(0, (8 * days) - totalHours);
        
        return Map.of(
            "totalHours", Math.round(totalHours * 10) / 10.0,
            "averageHours", Math.round(avgHours * 10) / 10.0,
            "averageScore", Math.round(avgScore),
            "sleepDebt", Math.round(sleepDebt * 10) / 10.0,
            "days", days,
            "entries", logs.size()
        );
    }
}
