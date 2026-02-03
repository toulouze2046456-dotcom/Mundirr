package com.health.controller;

import com.health.entity.ActivityLog;
import com.health.entity.User;
import com.health.repository.ActivityLogRepository;
import com.health.service.WgerExerciseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for Physical and Nutrition activity tracking.
 * Integrated with wger Workout Manager API for exercise data.
 */
@RestController
@RequestMapping("/api")
public class ActivityController extends BaseController {
    
    @Autowired
    private ActivityLogRepository activityRepo;
    
    @Autowired
    private WgerExerciseService wgerService;
    
    // ============ PHYSICAL WORKOUTS ============
    
    /**
     * Get all physical workouts for the authenticated user
     */
    @GetMapping("/physical")
    public List<ActivityLog> getPhysical(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return activityRepo.findByUserIdAndType(user.getId(), "PHYSICAL");
    }
    
    /**
     * Add a physical workout
     */
    @PostMapping("/physical")
    public ActivityLog addPhysical(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        int value = body.get("value") instanceof Number ? ((Number) body.get("value")).intValue() : 0;
        String date = (String) body.getOrDefault("date", LocalDate.now().toString());
        String label = (String) body.getOrDefault("label", "Workout");
        String exerciseId = (String) body.get("exerciseId");
        String muscleGroups = (String) body.get("muscleGroups");
        
        ActivityLog log = new ActivityLog(user.getId(), "PHYSICAL", date, value, 0, label);
        log.setExerciseId(exerciseId);
        log.setMuscleGroups(muscleGroups);
        
        return activityRepo.save(log);
    }
    
    /**
     * Delete a physical workout
     */
    @DeleteMapping("/physical/{id}")
    public ResponseEntity<?> deletePhysical(HttpServletRequest request, @PathVariable Long id) {
        User user = getAuthenticatedUser(request);
        
        return activityRepo.findById(id)
                .filter(log -> log.getUserId().equals(user.getId()))
                .map(log -> {
                    activityRepo.delete(log);
                    return ResponseEntity.ok(Map.of("success", true));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // ============ EXERCISE SEARCH (wger API) ============
    
    /**
     * Search exercises using wger Workout Manager API.
     */
    @GetMapping("/exercises/search")
    public List<Map<String, Object>> searchExercises(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "20") int limit) {
        return wgerService.searchExercises(query, category, limit);
    }
    
    /**
     * Get exercise details by ID.
     */
    @GetMapping("/exercises/{id}")
    public ResponseEntity<Map<String, Object>> getExerciseDetails(@PathVariable int id) {
        Map<String, Object> details = wgerService.getExerciseDetails(id);
        if (details.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }
    
    /**
     * Get exercises for a specific muscle group.
     */
    @GetMapping("/exercises/muscle/{muscleId}")
    public List<Map<String, Object>> getExercisesByMuscle(
            @PathVariable int muscleId,
            @RequestParam(defaultValue = "20") int limit) {
        return wgerService.getExercisesByMuscle(muscleId, limit);
    }
    
    /**
     * Get all muscle groups.
     */
    @GetMapping("/exercises/muscles")
    public List<Map<String, Object>> getMuscleGroups() {
        return wgerService.getMuscleGroups();
    }
    
    /**
     * Get all equipment types.
     */
    @GetMapping("/exercises/equipment")
    public List<Map<String, Object>> getEquipment() {
        return wgerService.getEquipment();
    }
    
    /**
     * Get all exercise categories.
     */
    @GetMapping("/exercises/categories")
    public List<Map<String, Object>> getCategories() {
        return wgerService.getCategories();
    }
    
    /**
     * Estimate calories burned for an exercise.
     */
    @GetMapping("/exercises/calories")
    public Map<String, Object> estimateCalories(
            @RequestParam String category,
            @RequestParam int durationMinutes,
            @RequestParam(defaultValue = "70") double weightKg) {
        int calories = wgerService.estimateCaloriesBurned(category, durationMinutes, weightKg);
        return Map.of(
                "category", category,
                "durationMinutes", durationMinutes,
                "weightKg", weightKg,
                "caloriesBurned", calories
        );
    }
    
    // ============ NUTRITION (Legacy ActivityLog-based) ============
    
    /**
     * Get all nutrition logs for the authenticated user
     */
    @GetMapping("/nutrition")
    public List<ActivityLog> getNutrition(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return activityRepo.findByUserIdAndType(user.getId(), "NUTRITION");
    }
    
    /**
     * Add a nutrition entry
     */
    @PostMapping("/nutrition")
    public ActivityLog addNutrition(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        int calories = body.get("value") instanceof Number ? ((Number) body.get("value")).intValue() : 0;
        int protein = body.get("protein") instanceof Number ? ((Number) body.get("protein")).intValue() : 0;
        String date = (String) body.getOrDefault("date", LocalDate.now().toString());
        String label = (String) body.getOrDefault("label", "Meal");
        
        return activityRepo.save(new ActivityLog(user.getId(), "NUTRITION", date, calories, protein, label));
    }
}
