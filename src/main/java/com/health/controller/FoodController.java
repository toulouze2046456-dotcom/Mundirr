package com.health.controller;

import com.health.entity.FoodLog;
import com.health.entity.User;
import com.health.repository.ActivityLogRepository;
import com.health.repository.FoodLogRepository;
import com.health.service.USDANutritionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Food logging with detailed macronutrient tracking.
 * Integrated with USDA FoodData Central API for comprehensive nutrition data.
 */
@RestController
@RequestMapping("/api")
public class FoodController extends BaseController {
    
    @Autowired
    private FoodLogRepository foodRepo;
    
    @Autowired
    private ActivityLogRepository activityRepo;
    
    @Autowired
    private USDANutritionService usdaService;
    
    // Fallback food database (used when USDA API is unavailable)
    private static final Map<String, int[]> FOOD_DB = Map.ofEntries(
        Map.entry("Chicken Breast", new int[]{165, 31, 4, 0}),   // cal, protein, fat, carbs
        Map.entry("Rice", new int[]{130, 3, 0, 28}),
        Map.entry("Broccoli", new int[]{34, 3, 0, 7}),
        Map.entry("Egg", new int[]{155, 13, 11, 1}),
        Map.entry("Salmon", new int[]{208, 20, 13, 0}),
        Map.entry("Avocado", new int[]{160, 2, 15, 9}),
        Map.entry("Salad", new int[]{20, 1, 0, 4}),
        Map.entry("Beef", new int[]{250, 26, 15, 0}),
        Map.entry("Pasta", new int[]{131, 5, 1, 25}),
        Map.entry("Bread", new int[]{265, 9, 3, 49}),
        Map.entry("Greek Yogurt", new int[]{100, 17, 1, 6}),
        Map.entry("Oatmeal", new int[]{150, 5, 3, 27}),
        Map.entry("Banana", new int[]{89, 1, 0, 23}),
        Map.entry("Apple", new int[]{52, 0, 0, 14}),
        Map.entry("Almonds", new int[]{579, 21, 50, 22}),
        Map.entry("Whey Protein", new int[]{120, 24, 1, 3}),
        Map.entry("Creatine", new int[]{0, 0, 0, 0})
    );
    
    /**
     * Get all food logs for the user
     */
    @GetMapping("/food")
    public List<FoodLog> getAll(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return foodRepo.findByUserId(user.getId());
    }
    
    /**
     * Get today's food logs
     */
    @GetMapping("/food/today")
    public List<FoodLog> getToday(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        String today = LocalDate.now().toString();
        return foodRepo.findByUserIdAndDate(user.getId(), today);
    }
    
    /**
     * Add a food log
     */
    @PostMapping("/food")
    public FoodLog addFood(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        String foodName = (String) body.getOrDefault("foodName", "Chicken Breast");
        double grams = body.get("grams") instanceof Number ? ((Number) body.get("grams")).doubleValue() : 100.0;
        String date = (String) body.getOrDefault("date", LocalDate.now().toString());
        String mealType = (String) body.getOrDefault("mealType", "Snack");
        String fdcId = (String) body.get("fdcId");
        
        // Look up in basic database or use provided values
        int[] macros = FOOD_DB.getOrDefault(foodName, new int[]{100, 10, 5, 15});
        
        // Allow override from request body (for API-sourced data)
        int calories = body.get("calories") instanceof Number 
            ? ((Number) body.get("calories")).intValue() 
            : (int) (macros[0] * grams / 100);
        int protein = body.get("protein") instanceof Number 
            ? ((Number) body.get("protein")).intValue() 
            : (int) (macros[1] * grams / 100);
        int fat = body.get("fat") instanceof Number 
            ? ((Number) body.get("fat")).intValue() 
            : (int) (macros[2] * grams / 100);
        
        FoodLog log = new FoodLog(user.getId(), foodName, grams, calories, protein, fat, date);
        log.setMealType(mealType);
        log.setFdcId(fdcId);
        
        // Set micronutrients if provided
        if (body.get("fiber") instanceof Number) log.setFiber(((Number) body.get("fiber")).doubleValue());
        if (body.get("sodium") instanceof Number) log.setSodium(((Number) body.get("sodium")).doubleValue());
        if (body.get("magnesium") instanceof Number) log.setMagnesium(((Number) body.get("magnesium")).doubleValue());
        
        return foodRepo.save(log);
    }
    
    /**
     * Delete a food log
     */
    @DeleteMapping("/food/{id}")
    public ResponseEntity<?> deleteFood(HttpServletRequest request, @PathVariable Long id) {
        User user = getAuthenticatedUser(request);
        
        return foodRepo.findById(id)
            .filter(log -> log.getUserId().equals(user.getId()))
            .map(log -> {
                foodRepo.delete(log);
                return ResponseEntity.ok(Map.of("success", true));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get nutrition balance (calories in vs out)
     */
    @GetMapping("/nutrition/balance")
    public Map<String, Object> getBalance(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        String today = LocalDate.now().toString();
        
        List<FoodLog> foods = foodRepo.findByUserIdAndDate(user.getId(), today);
        var workouts = activityRepo.findByUserIdAndTypeAndDate(user.getId(), "PHYSICAL", today);
        
        int caloriesIn = foods.stream().mapToInt(FoodLog::getCalories).sum();
        int totalProtein = foods.stream().mapToInt(FoodLog::getProtein).sum();
        int totalFat = foods.stream().mapToInt(FoodLog::getFat).sum();
        int totalCarbs = foods.stream().mapToInt(FoodLog::getCarbohydrates).sum();
        
        // Estimate calories burned (8 cal/min for moderate exercise)
        int caloriesBurned = workouts.stream()
            .mapToInt(w -> w.getValue() * 8)
            .sum();
        
        // Count veggie servings
        long veggieServings = foods.stream()
            .filter(f -> f.getFoodName().equals("Broccoli") || 
                        f.getFoodName().equals("Salad") ||
                        f.getFoodName().toLowerCase().contains("vegetable"))
            .count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("caloriesIn", caloriesIn);
        result.put("caloriesBurned", caloriesBurned);
        result.put("netCalories", caloriesIn - caloriesBurned);
        result.put("totalProtein", totalProtein);
        result.put("totalFat", totalFat);
        result.put("totalCarbs", totalCarbs);
        result.put("veggieServings", veggieServings);
        result.put("mealCount", foods.size());
        
        return result;
    }
    
    /**
     * Search foods using USDA FoodData Central API.
     * Falls back to local database if API is unavailable.
     */
    @GetMapping("/food/search")
    public List<Map<String, Object>> searchFood(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "true") boolean useUSDA) {
        
        if (useUSDA) {
            // Try USDA API first
            List<Map<String, Object>> usdaResults = usdaService.searchFoods(query, limit);
            if (!usdaResults.isEmpty()) {
                return usdaResults;
            }
        }
        
        // Fallback to local database
        return FOOD_DB.entrySet().stream()
                .filter(e -> e.getKey().toLowerCase().contains(query.toLowerCase()))
                .map(e -> Map.<String, Object>of(
                        "name", e.getKey(),
                        "calories", e.getValue()[0],
                        "protein", e.getValue()[1],
                        "fat", e.getValue()[2],
                        "carbs", e.getValue()[3],
                        "source", "local"
                ))
                .limit(limit)
                .toList();
    }
    
    /**
     * Get detailed nutrition info for a specific food by FDC ID.
     */
    @GetMapping("/food/details/{fdcId}")
    public ResponseEntity<Map<String, Object>> getFoodDetails(@PathVariable String fdcId) {
        Map<String, Object> details = usdaService.getFoodDetails(fdcId);
        if (details.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }
}
