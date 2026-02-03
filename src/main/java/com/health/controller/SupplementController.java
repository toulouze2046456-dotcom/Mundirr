package com.health.controller;

import com.health.entity.Supplement;
import com.health.entity.SupplementLog;
import com.health.entity.User;
import com.health.repository.SupplementLogRepository;
import com.health.repository.SupplementRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for Supplement stack management.
 */
@RestController
@RequestMapping("/api/supplements")
public class SupplementController extends BaseController {
    
    @Autowired
    private SupplementRepository suppRepo;
    
    @Autowired
    private SupplementLogRepository logRepo;
    
    /**
     * Get user's supplement stack with today's intake status
     */
    @GetMapping
    public List<Map<String, Object>> getStack(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        String today = LocalDate.now().toString();
        
        List<Supplement> supps = suppRepo.findByUserId(user.getId());
        List<SupplementLog> logs = logRepo.findByUserIdAndDate(user.getId(), today);
        
        Set<Long> takenIds = logs.stream()
            .map(SupplementLog::getSupplementId)
            .collect(Collectors.toSet());
        
        return supps.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("name", s.getName());
            map.put("dosage", s.getDosage());
            map.put("type", s.getType());
            map.put("frequency", s.getFrequency());
            map.put("timeOfDay", s.getTimeOfDay());
            map.put("notes", s.getNotes());
            map.put("taken", takenIds.contains(s.getId()));
            return map;
        }).collect(Collectors.toList());
    }
    
    /**
     * Add a new supplement to the stack
     */
    @PostMapping
    public Supplement add(HttpServletRequest request, @RequestBody Map<String, String> body) {
        User user = getAuthenticatedUser(request);
        
        String name = body.get("name");
        String dosage = body.getOrDefault("dosage", "");
        String type = body.getOrDefault("type", "Supplement");
        String frequency = body.getOrDefault("frequency", "daily");
        String timeOfDay = body.getOrDefault("timeOfDay", "morning");
        String notes = body.getOrDefault("notes", "");
        
        return suppRepo.save(new Supplement(user.getId(), name, dosage, type, frequency, timeOfDay, notes));
    }
    
    /**
     * Toggle supplement taken status for today
     */
    @PostMapping("/{id}/toggle")
    public Map<String, Object> toggle(HttpServletRequest request, @PathVariable Long id) {
        User user = getAuthenticatedUser(request);
        String today = LocalDate.now().toString();
        
        Optional<SupplementLog> existing = logRepo.findByUserIdAndSupplementIdAndDate(user.getId(), id, today);
        
        if (existing.isPresent()) {
            logRepo.delete(existing.get());
            return Map.of("taken", false, "message", "Marked as not taken");
        } else {
            logRepo.save(new SupplementLog(user.getId(), id, today, true));
            return Map.of("taken", true, "message", "Marked as taken");
        }
    }
    
    /**
     * Delete a supplement from the stack
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable Long id) {
        User user = getAuthenticatedUser(request);
        
        return suppRepo.findById(id)
            .filter(s -> s.getUserId().equals(user.getId()))
            .map(s -> {
                suppRepo.delete(s);
                return ResponseEntity.ok(Map.of("success", true));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update a supplement
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(HttpServletRequest request, @PathVariable Long id, 
                                    @RequestBody Map<String, String> body) {
        User user = getAuthenticatedUser(request);
        
        return suppRepo.findById(id)
            .filter(s -> s.getUserId().equals(user.getId()))
            .map(s -> {
                if (body.containsKey("name")) s.setName(body.get("name"));
                if (body.containsKey("dosage")) s.setDosage(body.get("dosage"));
                if (body.containsKey("type")) s.setType(body.get("type"));
                if (body.containsKey("frequency")) s.setFrequency(body.get("frequency"));
                if (body.containsKey("timeOfDay")) s.setTimeOfDay(body.get("timeOfDay"));
                if (body.containsKey("notes")) s.setNotes(body.get("notes"));
                return ResponseEntity.ok(suppRepo.save(s));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get supplement intake logs for a specific date
     */
    @GetMapping("/logs")
    public List<SupplementLog> getLogs(HttpServletRequest request, @RequestParam String date) {
        User user = getAuthenticatedUser(request);
        return logRepo.findByUserIdAndDate(user.getId(), date);
    }
    
    /**
     * Log a supplement intake for a specific date
     */
    @PostMapping("/log")
    public SupplementLog logIntake(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        Long supplementId = body.get("supplementId") instanceof Number 
            ? ((Number) body.get("supplementId")).longValue() : 0L;
        String date = (String) body.getOrDefault("date", LocalDate.now().toString());
        
        // Check if already logged for this date
        Optional<SupplementLog> existing = logRepo.findByUserIdAndSupplementIdAndDate(
            user.getId(), supplementId, date);
        
        if (existing.isPresent()) {
            return existing.get();
        }
        
        return logRepo.save(new SupplementLog(user.getId(), supplementId, date, true));
    }
}
