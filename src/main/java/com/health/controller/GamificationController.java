package com.health.controller;

import com.health.entity.*;
import com.health.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Controller for Gamification and overall health scoring.
 */
@RestController
@RequestMapping("/api")
public class GamificationController extends BaseController {
    
    @Autowired
    private ActivityLogRepository activityRepo;
    
    @Autowired
    private CulturalActivityRepository culturalRepo;
    
    @Autowired
    private SleepLogRepository sleepRepo;
    
    @Autowired
    private FinanceTransactionRepository financeRepo;
    
    @Autowired
    private SubstanceLogRepository substanceRepo;
    
    @Autowired
    private AlcoholLogRepository alcoholRepo;
    
    @Autowired
    private FoodLogRepository foodRepo;
    
    /**
     * Get gamification stats (XP, level, badges, tokens)
     */
    @GetMapping("/gamification")
    public Map<String, Object> getGamification(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        Long userId = user.getId();
        
        // Calculate XP from different activities
        int workoutXP = activityRepo.findByUserIdAndType(userId, "PHYSICAL").size() * 50;
        int culturalXP = culturalRepo.findByUserId(userId).size() * 30;
        int sleepXP = sleepRepo.findByUserIdOrderByDateDesc(userId).size() * 20;
        int nutritionXP = foodRepo.findByUserId(userId).size() * 15;
        int financeXP = financeRepo.findByUserId(userId).size() * 10;
        int baseXP = 1500;
        
        int totalXP = baseXP + workoutXP + culturalXP + sleepXP + nutritionXP + financeXP;
        int level = (int) Math.sqrt(totalXP / 100.0);
        int tokens = totalXP / 10;
        
        // Calculate badges
        List<Map<String, Object>> badges = new ArrayList<>();
        
        if (sleepXP >= 100) badges.add(Map.of("id", "early_bird", "name", "Early Bird", "icon", "🌅"));
        if (workoutXP >= 50) badges.add(Map.of("id", "iron_pumper", "name", "Iron Pumper", "icon", "💪"));
        if (culturalXP >= 30) badges.add(Map.of("id", "bookworm", "name", "Bookworm", "icon", "📚"));
        if (financeXP >= 10) badges.add(Map.of("id", "wealth_builder", "name", "Wealth Builder", "icon", "💰"));
        if (nutritionXP >= 50) badges.add(Map.of("id", "nutrition_master", "name", "Nutrition Master", "icon", "🥗"));
        if (level >= 10) badges.add(Map.of("id", "veteran", "name", "Veteran", "icon", "⭐"));
        if (level >= 25) badges.add(Map.of("id", "elite", "name", "Elite", "icon", "👑"));
        
        Map<String, Object> result = new HashMap<>();
        result.put("xp", totalXP);
        result.put("level", level);
        result.put("tokens", tokens);
        result.put("badges", badges);
        result.put("breakdown", Map.of(
            "workoutXP", workoutXP,
            "culturalXP", culturalXP,
            "sleepXP", sleepXP,
            "nutritionXP", nutritionXP,
            "financeXP", financeXP
        ));
        result.put("nextLevelXP", (int) Math.pow(level + 1, 2) * 100);
        
        return result;
    }
    
    /**
     * Get comprehensive health scores across all dimensions
     */
    @GetMapping("/health-scores")
    public Map<String, Object> getHealthScores(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        Long userId = user.getId();
        
        String today = LocalDate.now().toString();
        String weekAgo = LocalDate.now().minusDays(7).toString();
        
        // Physical Score (based on recent workouts)
        List<ActivityLog> workouts = activityRepo.findByUserIdAndType(userId, "PHYSICAL");
        int recentWorkouts = (int) workouts.stream()
            .filter(w -> w.getDate() != null && w.getDate().compareTo(weekAgo) >= 0)
            .count();
        int totalWorkoutMins = workouts.stream()
            .filter(w -> w.getDate() != null && w.getDate().compareTo(weekAgo) >= 0)
            .mapToInt(ActivityLog::getValue)
            .sum();
        double physicalScore = Math.min(100, (recentWorkouts * 15) + (totalWorkoutMins / 10.0));
        
        // Nutrition Score
        List<FoodLog> foods = foodRepo.findByUserIdAndDate(userId, today);
        int todayProtein = foods.stream().mapToInt(FoodLog::getProtein).sum();
        int todayCalories = foods.stream().mapToInt(FoodLog::getCalories).sum();
        double nutritionScore = Math.min(100, (todayProtein / 1.5) + (todayCalories > 0 ? 20 : 0));
        
        // Sleep Score (average of recent)
        double sleepScore = sleepRepo.findByUserIdOrderByDateDesc(userId).stream()
            .limit(7)
            .mapToInt(SleepLog::getScore)
            .average()
            .orElse(0);
        
        // Substance Score (lower is better)
        List<SubstanceLog> substances = substanceRepo.findByUserIdAndDateGreaterThanEqual(userId, weekAgo);
        List<AlcoholLog> alcohol = alcoholRepo.findByUserIdAndDateGreaterThanEqual(userId, weekAgo);
        int totalCigarettes = substances.stream().mapToInt(SubstanceLog::getCount).sum();
        int totalAlcohol = alcohol.stream().mapToInt(AlcoholLog::getUnits).sum();
        double substanceScore = Math.max(0, 100 - (totalCigarettes * 5) - (totalAlcohol * 3));
        
        // Hormone/Resonance Score (calculated from substances and activity)
        double hormoneScore = 50.0;
        hormoneScore += (totalWorkoutMins / 45.0) * 10.0; // Exercise boosts testosterone
        hormoneScore -= (totalAlcohol * 4.0);              // Alcohol suppresses testosterone
        hormoneScore -= (totalCigarettes * 2.0);           // Smoking increases SHBG
        hormoneScore = Math.max(0, Math.min(100, hormoneScore));
        
        // Finance Score
        var financeStats = financeRepo.findByUserId(userId);
        double income = financeStats.stream()
            .filter(t -> "INCOME".equals(t.getType()))
            .mapToDouble(FinanceTransaction::getAmount)
            .sum();
        double expenses = financeStats.stream()
            .filter(t -> "EXPENSE".equals(t.getType()))
            .mapToDouble(FinanceTransaction::getAmount)
            .sum();
        double savingsRate = income > 0 ? ((income - expenses) / income) * 100 : 0;
        double financeScore = Math.max(0, Math.min(100, 50 + savingsRate));
        
        // Overall Score (weighted average)
        double overallScore = (
            physicalScore * 0.2 +
            nutritionScore * 0.15 +
            sleepScore * 0.2 +
            substanceScore * 0.15 +
            hormoneScore * 0.15 +
            financeScore * 0.15
        );
        
        List<Map<String, Object>> scores = List.of(
            Map.of("label", "Activity", "score", (int) Math.round(physicalScore), "color", "#22c55e"),
            Map.of("label", "Nutrition", "score", (int) Math.round(nutritionScore), "color", "#f97316"),
            Map.of("label", "Sleep", "score", (int) Math.round(sleepScore), "color", "#8b5cf6"),
            Map.of("label", "Substances", "score", (int) Math.round(substanceScore), "color", "#ef4444"),
            Map.of("label", "Hormones", "score", (int) Math.round(hormoneScore), "color", "#06b6d4"),
            Map.of("label", "Finance", "score", (int) Math.round(financeScore), "color", "#eab308")
        );
        
        return Map.of(
            "overall", (int) Math.round(overallScore),
            "scores", scores,
            "details", Map.of(
                "totalWorkouts", workouts.size(),
                "totalCigarettes", totalCigarettes,
                "totalAlcohol", totalAlcohol,
                "todayProtein", todayProtein,
                "todayCalories", todayCalories
            )
        );
    }
    
    /**
     * Health check endpoint (public)
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        return Map.of(
            "status", "UP",
            "name", "Vellbeing OS",
            "version", "1.0.0",
            "timestamp", LocalDate.now().toString()
        );
    }
}
