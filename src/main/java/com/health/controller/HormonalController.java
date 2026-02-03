package com.health.controller;

import com.health.entity.User;
import com.health.service.HormonalImpactCalculator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for Hormonal Impact calculations.
 * Provides scientific data about substance effects on hormones and sleep.
 */
@RestController
@RequestMapping("/api/hormonal")
public class HormonalController extends BaseController {
    
    @Autowired
    private HormonalImpactCalculator calculator;
    
    /**
     * Get all scientific constants used in calculations.
     */
    @GetMapping("/constants")
    public Map<String, Object> getConstants() {
        return calculator.getAllConstants();
    }
    
    /**
     * Calculate remaining caffeine in system.
     * 
     * @param initialMg Initial caffeine amount (mg)
     * @param hoursElapsed Hours since consumption
     * @param isSmoker Whether user is a smoker (affects metabolism)
     */
    @GetMapping("/caffeine/remaining")
    public Map<String, Object> getRemainingCaffeine(
            @RequestParam double initialMg,
            @RequestParam double hoursElapsed,
            @RequestParam(defaultValue = "false") boolean isSmoker) {
        
        double halfLife = isSmoker ? 
                HormonalImpactCalculator.CAFFEINE_HALF_LIFE_SMOKERS : 
                HormonalImpactCalculator.CAFFEINE_HALF_LIFE_HOURS;
        
        double remaining = calculator.calculateRemainingCaffeine(initialMg, hoursElapsed, halfLife);
        double hoursUntilClear = calculator.calculateHoursUntilCaffeineThreshold(
                remaining, 
                HormonalImpactCalculator.CAFFEINE_EFFECT_THRESHOLD_MG, 
                halfLife
        );
        
        boolean stillActive = remaining > HormonalImpactCalculator.CAFFEINE_EFFECT_THRESHOLD_MG;
        
        return Map.of(
                "initialMg", initialMg,
                "remainingMg", Math.round(remaining * 10) / 10.0,
                "percentRemaining", Math.round((remaining / initialMg) * 100),
                "halfLifeUsed", halfLife,
                "stillActive", stillActive,
                "hoursUntilClear", Math.round(hoursUntilClear * 10) / 10.0,
                "effectThreshold", HormonalImpactCalculator.CAFFEINE_EFFECT_THRESHOLD_MG
        );
    }
    
    /**
     * Calculate current BAC (Blood Alcohol Content).
     */
    @GetMapping("/alcohol/bac")
    public Map<String, Object> calculateBAC(
            @RequestParam double drinks,
            @RequestParam double hoursElapsed,
            @RequestParam double weightKg,
            @RequestParam(defaultValue = "true") boolean isMale) {
        
        double bac = calculator.calculateBAC(drinks, hoursElapsed, weightKg, isMale);
        
        String impairmentLevel = bac >= 0.08 ? "Legally Impaired" :
                                 bac >= 0.05 ? "Impaired" :
                                 bac >= 0.02 ? "Mildly Affected" : "Sober";
        
        // Estimate time until sober
        double hoursUntilSober = bac > 0 ? bac / 0.015 : 0;
        
        return Map.of(
                "bac", bac,
                "impairmentLevel", impairmentLevel,
                "hoursUntilSober", Math.round(hoursUntilSober * 10) / 10.0,
                "legalLimit", 0.08,
                "remSleepImpactHours", drinks * HormonalImpactCalculator.REM_SUPPRESSION_HOURS_PER_DRINK,
                "gabaRecoveryHours", drinks > 4 ? 
                        HormonalImpactCalculator.GABA_RECOVERY_HOURS_HEAVY : 
                        HormonalImpactCalculator.GABA_RECOVERY_HOURS_MODERATE
        );
    }
    
    /**
     * Calculate remaining nicotine and effects.
     */
    @GetMapping("/nicotine/remaining")
    public Map<String, Object> getRemainingNicotine(
            @RequestParam int cigarettes,
            @RequestParam double hoursElapsed) {
        
        double remaining = calculator.calculateRemainingNicotine(cigarettes, hoursElapsed);
        double initial = cigarettes * HormonalImpactCalculator.NICOTINE_PER_CIGARETTE_MG;
        
        return Map.of(
                "cigarettes", cigarettes,
                "initialNicotineMg", initial,
                "remainingNicotineMg", Math.round(remaining * 100) / 100.0,
                "percentRemaining", Math.round((remaining / initial) * 100),
                "halfLifeHours", HormonalImpactCalculator.NICOTINE_HALF_LIFE_HOURS,
                "cotinineHalfLifeHours", HormonalImpactCalculator.COTININE_HALF_LIFE_HOURS,
                "heartRateImpactBpm", cigarettes * HormonalImpactCalculator.NICOTINE_HEART_RATE_INCREASE_BPM
        );
    }
    
    /**
     * Calculate adenosine pressure (sleep drive).
     */
    @GetMapping("/sleep/adenosine")
    public Map<String, Object> getAdenosinePressure(
            @RequestParam double hoursAwake,
            @RequestParam(defaultValue = "7") double lastSleepDuration) {
        
        double pressure = calculator.calculateAdenosinePressure(hoursAwake, lastSleepDuration);
        
        String sleepDriveLevel = pressure > 0.8 ? "Very High" :
                                 pressure > 0.6 ? "High" :
                                 pressure > 0.4 ? "Moderate" :
                                 pressure > 0.2 ? "Low" : "Very Low";
        
        return Map.of(
                "hoursAwake", hoursAwake,
                "lastSleepHours", lastSleepDuration,
                "adenosinePressure", Math.round(pressure * 100),
                "sleepDriveLevel", sleepDriveLevel,
                "idealSleepOnset", pressure > 0.6 ? "Soon" : "Later"
        );
    }
    
    /**
     * Calculate predicted sleep quality impact from various factors.
     */
    @GetMapping("/sleep/quality-impact")
    public Map<String, Object> getSleepQualityImpact(
            @RequestParam(defaultValue = "0") double caffeineInSystem,
            @RequestParam(defaultValue = "0") int alcoholDrinks,
            @RequestParam(defaultValue = "24") double exerciseHoursAgo,
            @RequestParam(defaultValue = "0") int screenTimeMinutes) {
        
        int impact = calculator.calculateSleepQualityImpact(
                caffeineInSystem, alcoholDrinks, exerciseHoursAgo, screenTimeMinutes);
        
        String qualityPrediction = impact > 10 ? "Enhanced" :
                                   impact > -10 ? "Normal" :
                                   impact > -30 ? "Reduced" :
                                   impact > -50 ? "Poor" : "Very Poor";
        
        return Map.of(
                "sleepQualityModifier", impact,
                "qualityPrediction", qualityPrediction,
                "factors", Map.of(
                        "caffeineImpact", caffeineInSystem > 30 ? "Negative" : "Neutral",
                        "alcoholImpact", alcoholDrinks > 0 ? "Negative (REM disruption)" : "Neutral",
                        "exerciseImpact", exerciseHoursAgo >= 3 && exerciseHoursAgo <= 8 ? "Positive" : 
                                         exerciseHoursAgo < 3 ? "Negative (too close)" : "Neutral",
                        "screenTimeImpact", screenTimeMinutes > 30 ? "Negative (melatonin suppression)" : "Neutral"
                ),
                "recommendations", generateSleepRecommendations(caffeineInSystem, alcoholDrinks, exerciseHoursAgo, screenTimeMinutes)
        );
    }
    
    /**
     * Get comprehensive hormonal status.
     */
    @GetMapping("/status")
    public Map<String, Object> getHormonalStatus(
            @RequestParam(defaultValue = "0") double caffeineInSystem,
            @RequestParam(defaultValue = "8") double hoursAwake,
            @RequestParam(defaultValue = "false") boolean recentExercise) {
        
        return calculator.getHormonalStatus(caffeineInSystem, hoursAwake, recentExercise);
    }
    
    /**
     * Get recommended time gap between substance intake and sleep.
     */
    @GetMapping("/sleep-gap")
    public Map<String, Object> getRecommendedSleepGap(
            @RequestParam String substance,
            @RequestParam double amount) {
        
        double gap = calculator.getRecommendedSleepGap(substance, amount);
        
        return Map.of(
                "substance", substance,
                "amount", amount,
                "recommendedGapHours", Math.round(gap * 10) / 10.0,
                "reason", getSleepGapReason(substance)
        );
    }
    
    /**
     * Get caffeine content of common beverages.
     */
    @GetMapping("/caffeine/drinks")
    public Map<String, Integer> getCaffeineDrinks() {
        return HormonalImpactCalculator.CAFFEINE_CONTENT_MG;
    }
    
    // ============ HELPER METHODS ============
    
    private java.util.List<String> generateSleepRecommendations(
            double caffeine, int alcohol, double exerciseHours, int screenTime) {
        
        java.util.List<String> recommendations = new java.util.ArrayList<>();
        
        if (caffeine > 50) {
            recommendations.add("Wait " + Math.round(
                    calculator.calculateHoursUntilCaffeineThreshold(caffeine, 30, 5) * 10) / 10.0 + 
                    " more hours before sleeping");
        }
        if (alcohol > 2) {
            recommendations.add("Alcohol will disrupt REM sleep for ~" + 
                    Math.round(alcohol * 1.5 * 10) / 10.0 + " hours");
        }
        if (exerciseHours < 3) {
            recommendations.add("Body temperature may still be elevated; consider relaxation techniques");
        }
        if (screenTime > 30) {
            recommendations.add("Blue light exposure detected; consider blue light glasses or night mode");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("No major sleep disruptors detected");
        }
        
        return recommendations;
    }
    
    private String getSleepGapReason(String substance) {
        return switch (substance.toLowerCase()) {
            case "caffeine", "coffee", "tea" -> "Caffeine blocks adenosine receptors and has a half-life of 5 hours";
            case "alcohol" -> "Alcohol disrupts REM sleep and takes ~1 hour per drink to metabolize";
            case "nicotine", "cigarettes" -> "Nicotine elevates heart rate and cortisol levels";
            case "sugar" -> "Sugar can cause reactive hypoglycemia 3-4 hours after consumption";
            case "exercise" -> "Core body temperature needs to drop for optimal sleep onset";
            default -> "General recommendation for substance metabolism";
        };
    }
}
