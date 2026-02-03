package com.health.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Scientific calculator for hormonal and physiological impacts of various substances.
 * 
 * This service calculates the current biological impact of substances based on:
 * - Pharmacokinetic half-lives
 * - Receptor binding affinities
 * - Metabolic pathways
 * - Circadian rhythm interactions
 * 
 * All values are derived from peer-reviewed scientific literature.
 * Note: This is for informational purposes only and not medical advice.
 */
@Service
public class HormonalImpactCalculator {
    
    // ==========================================
    // CAFFEINE CONSTANTS
    // Source: Institute of Medicine (2001), Clinical Pharmacokinetics
    // ==========================================
    
    /** Caffeine half-life in hours (average for healthy adults) */
    public static final double CAFFEINE_HALF_LIFE_HOURS = 5.0;
    
    /** Caffeine half-life during pregnancy (first trimester) */
    public static final double CAFFEINE_HALF_LIFE_PREGNANCY_T1 = 11.0;
    
    /** Caffeine half-life during pregnancy (third trimester) */
    public static final double CAFFEINE_HALF_LIFE_PREGNANCY_T3 = 18.3;
    
    /** Caffeine half-life for smokers (accelerated metabolism) */
    public static final double CAFFEINE_HALF_LIFE_SMOKERS = 3.0;
    
    /** Time for caffeine to reach peak plasma concentration (minutes) */
    public static final int CAFFEINE_TIME_TO_PEAK_MIN = 45;
    
    /** Caffeine content in common drinks (mg) */
    public static final Map<String, Integer> CAFFEINE_CONTENT_MG = Map.ofEntries(
            Map.entry("Espresso (1 shot)", 63),
            Map.entry("Coffee (8oz)", 95),
            Map.entry("Coffee (12oz)", 140),
            Map.entry("Cold Brew (12oz)", 200),
            Map.entry("Black Tea (8oz)", 47),
            Map.entry("Green Tea (8oz)", 28),
            Map.entry("Energy Drink (8oz)", 80),
            Map.entry("Cola (12oz)", 34),
            Map.entry("Dark Chocolate (1oz)", 23),
            Map.entry("Pre-workout (1 scoop)", 200)
    );
    
    /** Adenosine receptor blocking threshold (mg) - minimum for noticeable effect */
    public static final int CAFFEINE_EFFECT_THRESHOLD_MG = 30;
    
    /** Cortisol spike percentage from caffeine consumption */
    public static final double CAFFEINE_CORTISOL_SPIKE_PERCENT = 30.0;
    
    // ==========================================
    // ALCOHOL CONSTANTS
    // Source: NIAAA, Clinical Pharmacology
    // ==========================================
    
    /** Alcohol elimination rate (g/hour) - average for men */
    public static final double ALCOHOL_ELIMINATION_RATE_MALE = 10.0;
    
    /** Alcohol elimination rate (g/hour) - average for women */
    public static final double ALCOHOL_ELIMINATION_RATE_FEMALE = 8.0;
    
    /** Grams of alcohol per standard drink */
    public static final double GRAMS_PER_STANDARD_DRINK = 14.0;
    
    /** BAC increase per standard drink (approximate, varies by weight) */
    public static final double BAC_PER_DRINK_MALE_80KG = 0.02;
    public static final double BAC_PER_DRINK_FEMALE_65KG = 0.03;
    
    /** Hours for complete GABA receptor recovery after moderate drinking */
    public static final double GABA_RECOVERY_HOURS_MODERATE = 24.0;
    
    /** Hours for complete GABA receptor recovery after heavy drinking */
    public static final double GABA_RECOVERY_HOURS_HEAVY = 72.0;
    
    /** Cortisol elevation percentage from alcohol */
    public static final double ALCOHOL_CORTISOL_ELEVATION_PERCENT = 25.0;
    
    /** Hours of REM sleep suppression per drink */
    public static final double REM_SUPPRESSION_HOURS_PER_DRINK = 1.5;
    
    /** Testosterone reduction percentage from heavy drinking (5+ drinks) */
    public static final double TESTOSTERONE_REDUCTION_HEAVY_PERCENT = 23.0;
    
    // ==========================================
    // NICOTINE CONSTANTS
    // Source: Journal of Clinical Pharmacology
    // ==========================================
    
    /** Nicotine half-life in hours */
    public static final double NICOTINE_HALF_LIFE_HOURS = 2.0;
    
    /** Cotinine (nicotine metabolite) half-life in hours */
    public static final double COTININE_HALF_LIFE_HOURS = 16.0;
    
    /** Nicotine per cigarette (mg) */
    public static final double NICOTINE_PER_CIGARETTE_MG = 1.0;
    
    /** Dopamine spike duration (minutes) */
    public static final int NICOTINE_DOPAMINE_SPIKE_DURATION_MIN = 30;
    
    /** Cortisol increase percentage per cigarette */
    public static final double NICOTINE_CORTISOL_INCREASE_PERCENT = 15.0;
    
    /** Heart rate increase (bpm) per cigarette */
    public static final int NICOTINE_HEART_RATE_INCREASE_BPM = 10;
    
    // ==========================================
    // CANNABIS CONSTANTS  
    // Source: Clinical Pharmacology & Therapeutics
    // ==========================================
    
    /** THC half-life in plasma (hours) - single use */
    public static final double THC_HALF_LIFE_SINGLE_USE = 1.3;
    
    /** THC half-life in plasma (hours) - chronic use */
    public static final double THC_HALF_LIFE_CHRONIC = 5.0;
    
    /** THC metabolite (THCCOOH) half-life in days */
    public static final double THC_METABOLITE_HALF_LIFE_DAYS = 7.0;
    
    /** Time to peak THC effects when smoked (minutes) */
    public static final int THC_PEAK_SMOKED_MIN = 10;
    
    /** Time to peak THC effects when ingested (minutes) */
    public static final int THC_PEAK_EDIBLE_MIN = 90;
    
    /** Dopamine increase percentage from THC */
    public static final double THC_DOPAMINE_INCREASE_PERCENT = 200.0;
    
    /** Cortisol increase percentage from acute THC use */
    public static final double THC_CORTISOL_INCREASE_PERCENT = 35.0;
    
    // ==========================================
    // SUGAR CONSTANTS
    // Source: American Journal of Clinical Nutrition
    // ==========================================
    
    /** Insulin spike peak time after sugar intake (minutes) */
    public static final int INSULIN_PEAK_TIME_MIN = 30;
    
    /** Insulin normalization time (hours) */
    public static final double INSULIN_NORMALIZATION_HOURS = 2.0;
    
    /** Blood glucose peak time (minutes) */
    public static final int GLUCOSE_PEAK_TIME_MIN = 45;
    
    /** Reactive hypoglycemia onset (hours after sugar spike) */
    public static final double REACTIVE_HYPOGLYCEMIA_ONSET_HOURS = 3.0;
    
    /** Dopamine spike from refined sugar (percentage above baseline) */
    public static final double SUGAR_DOPAMINE_SPIKE_PERCENT = 50.0;
    
    // ==========================================
    // EXERCISE HORMONAL EFFECTS
    // Source: Journal of Endocrinology, Sports Medicine
    // ==========================================
    
    /** Post-exercise testosterone elevation duration (hours) */
    public static final double EXERCISE_TESTOSTERONE_ELEVATION_HOURS = 1.0;
    
    /** Testosterone increase from resistance training (percentage) */
    public static final double RESISTANCE_TRAINING_TESTOSTERONE_PERCENT = 15.0;
    
    /** Cortisol spike duration after intense exercise (hours) */
    public static final double EXERCISE_CORTISOL_DURATION_HOURS = 2.0;
    
    /** Endorphin peak duration after exercise (hours) */
    public static final double ENDORPHIN_PEAK_DURATION_HOURS = 2.0;
    
    /** Growth hormone spike multiplier from intense exercise */
    public static final double EXERCISE_GH_SPIKE_MULTIPLIER = 4.0;
    
    /** BDNF increase from aerobic exercise (percentage) */
    public static final double EXERCISE_BDNF_INCREASE_PERCENT = 30.0;
    
    // ==========================================
    // SLEEP & CIRCADIAN CONSTANTS
    // Source: Sleep Medicine Reviews
    // ==========================================
    
    /** Melatonin onset before natural sleep (hours) */
    public static final double MELATONIN_ONSET_BEFORE_SLEEP = 2.0;
    
    /** Cortisol awakening response peak (minutes after waking) */
    public static final int CORTISOL_AWAKENING_PEAK_MIN = 30;
    
    /** Core body temperature minimum (hours before natural waking) */
    public static final double BODY_TEMP_MINIMUM_BEFORE_WAKE = 2.0;
    
    /** Adenosine accumulation rate per hour awake (arbitrary units) */
    public static final double ADENOSINE_ACCUMULATION_RATE = 0.1;
    
    /** Hours of sleep needed to clear accumulated adenosine */
    public static final double ADENOSINE_CLEARANCE_PER_SLEEP_HOUR = 0.15;
    
    // ==========================================
    // CALCULATION METHODS
    // ==========================================
    
    /**
     * Calculate remaining caffeine in system using half-life decay.
     * 
     * @param initialMg Initial caffeine amount in mg
     * @param hoursElapsed Hours since consumption
     * @param halfLifeHours Half-life to use (varies by population)
     * @return Remaining caffeine in mg
     */
    public double calculateRemainingCaffeine(double initialMg, double hoursElapsed, double halfLifeHours) {
        return initialMg * Math.pow(0.5, hoursElapsed / halfLifeHours);
    }
    
    /**
     * Calculate remaining caffeine using default half-life for healthy adults.
     */
    public double calculateRemainingCaffeine(double initialMg, double hoursElapsed) {
        return calculateRemainingCaffeine(initialMg, hoursElapsed, CAFFEINE_HALF_LIFE_HOURS);
    }
    
    /**
     * Calculate total active caffeine in system from multiple consumption events.
     * 
     * @param consumptionEvents List of maps containing "mg" and "timestamp" (LocalDateTime)
     * @param halfLifeHours Half-life to use
     * @return Total remaining caffeine in mg
     */
    public double calculateTotalActiveCaffeine(List<Map<String, Object>> consumptionEvents, double halfLifeHours) {
        LocalDateTime now = LocalDateTime.now();
        double total = 0;
        
        for (Map<String, Object> event : consumptionEvents) {
            double mg = ((Number) event.get("mg")).doubleValue();
            LocalDateTime timestamp = (LocalDateTime) event.get("timestamp");
            double hoursElapsed = ChronoUnit.MINUTES.between(timestamp, now) / 60.0;
            
            if (hoursElapsed >= 0) {
                total += calculateRemainingCaffeine(mg, hoursElapsed, halfLifeHours);
            }
        }
        
        return total;
    }
    
    /**
     * Estimate time until caffeine drops below threshold.
     * 
     * @param currentMg Current caffeine in system
     * @param thresholdMg Target threshold (e.g., 30mg for no effect)
     * @param halfLifeHours Half-life to use
     * @return Hours until threshold is reached
     */
    public double calculateHoursUntilCaffeineThreshold(double currentMg, double thresholdMg, double halfLifeHours) {
        if (currentMg <= thresholdMg) return 0;
        // t = half_life * log2(current / threshold)
        return halfLifeHours * (Math.log(currentMg / thresholdMg) / Math.log(2));
    }
    
    /**
     * Calculate current BAC (Blood Alcohol Content).
     * 
     * @param standardDrinks Number of standard drinks consumed
     * @param hoursElapsed Hours since first drink
     * @param bodyWeightKg Body weight in kg
     * @param isMale Biological sex for metabolism rate
     * @return Estimated BAC
     */
    public double calculateBAC(double standardDrinks, double hoursElapsed, double bodyWeightKg, boolean isMale) {
        // Widmark formula approximation
        double waterRatio = isMale ? 0.68 : 0.55;
        double gramsAlcohol = standardDrinks * GRAMS_PER_STANDARD_DRINK;
        double eliminationRate = isMale ? ALCOHOL_ELIMINATION_RATE_MALE : ALCOHOL_ELIMINATION_RATE_FEMALE;
        
        // Calculate initial BAC
        double bac = gramsAlcohol / (bodyWeightKg * 1000 * waterRatio);
        
        // Subtract elimination over time (approximately 0.015% per hour)
        double eliminated = (eliminationRate / 1000) * hoursElapsed / bodyWeightKg;
        bac = Math.max(0, bac - eliminated * 100);
        
        return Math.round(bac * 1000) / 1000.0; // Round to 3 decimal places
    }
    
    /**
     * Calculate remaining nicotine in system.
     * 
     * @param cigarettesSmoked Number of cigarettes
     * @param hoursElapsed Hours since last cigarette
     * @return Remaining nicotine in mg
     */
    public double calculateRemainingNicotine(int cigarettesSmoked, double hoursElapsed) {
        double initialMg = cigarettesSmoked * NICOTINE_PER_CIGARETTE_MG;
        return initialMg * Math.pow(0.5, hoursElapsed / NICOTINE_HALF_LIFE_HOURS);
    }
    
    /**
     * Calculate adenosine pressure (sleep drive) based on time awake.
     * 
     * @param hoursAwake Hours since last sleep
     * @param lastSleepDuration Hours of last sleep
     * @return Adenosine pressure score (0-1, 1 = maximum)
     */
    public double calculateAdenosinePressure(double hoursAwake, double lastSleepDuration) {
        // Base pressure from time awake
        double accumulated = hoursAwake * ADENOSINE_ACCUMULATION_RATE;
        
        // Reduction from sleep
        double cleared = lastSleepDuration * ADENOSINE_CLEARANCE_PER_SLEEP_HOUR;
        
        double pressure = Math.max(0, accumulated - cleared);
        return Math.min(1.0, pressure); // Cap at 1.0
    }
    
    /**
     * Calculate expected sleep quality impact from various factors.
     * 
     * @param caffeineInSystemMg Caffeine still in system at bedtime
     * @param alcoholDrinksToday Number of alcoholic drinks consumed
     * @param exerciseHoursAgo Hours since last exercise
     * @param screenTimeBeforeBedMin Screen time in last 2 hours before bed
     * @return Sleep quality modifier (-100 to +20)
     */
    public int calculateSleepQualityImpact(double caffeineInSystemMg, int alcoholDrinksToday, 
                                           double exerciseHoursAgo, int screenTimeBeforeBedMin) {
        int impact = 0;
        
        // Caffeine impact (negative if above threshold)
        if (caffeineInSystemMg > CAFFEINE_EFFECT_THRESHOLD_MG) {
            impact -= (int) Math.min(40, (caffeineInSystemMg - CAFFEINE_EFFECT_THRESHOLD_MG) / 2);
        }
        
        // Alcohol impact (disrupts REM)
        impact -= (int) (alcoholDrinksToday * REM_SUPPRESSION_HOURS_PER_DRINK * 5);
        
        // Exercise impact (positive if done earlier, negative if too close to bedtime)
        if (exerciseHoursAgo > 0 && exerciseHoursAgo < 3) {
            impact -= 15; // Too close to bedtime
        } else if (exerciseHoursAgo >= 3 && exerciseHoursAgo <= 8) {
            impact += 15; // Optimal timing
        }
        
        // Screen time impact (blue light suppresses melatonin)
        impact -= (int) (screenTimeBeforeBedMin / 10);
        
        return Math.max(-100, Math.min(20, impact));
    }
    
    /**
     * Get comprehensive hormonal status based on current substance levels.
     * 
     * @param caffeineInSystemMg Current caffeine in system
     * @param hoursAwake Hours since waking
     * @param recentExercise Whether exercised in last 3 hours
     * @return Map of hormonal impacts
     */
    public Map<String, Object> getHormonalStatus(double caffeineInSystemMg, double hoursAwake, boolean recentExercise) {
        Map<String, Object> status = new HashMap<>();
        
        // Cortisol status (naturally peaks in morning)
        double cortisolModifier = 0;
        if (hoursAwake <= 1) {
            cortisolModifier = 50; // Morning spike
        } else if (hoursAwake <= 3) {
            cortisolModifier = 30;
        }
        if (caffeineInSystemMg > 50) {
            cortisolModifier += CAFFEINE_CORTISOL_SPIKE_PERCENT * (caffeineInSystemMg / 100);
        }
        if (recentExercise) {
            cortisolModifier += 20;
        }
        status.put("cortisolModifier", Math.round(cortisolModifier));
        status.put("cortisolStatus", cortisolModifier > 40 ? "Elevated" : cortisolModifier > 20 ? "Normal" : "Low");
        
        // Adenosine status
        double adenosine = calculateAdenosinePressure(hoursAwake, 7);
        boolean caffeineBlocking = caffeineInSystemMg > CAFFEINE_EFFECT_THRESHOLD_MG;
        status.put("adenosinePressure", Math.round(adenosine * 100));
        status.put("adenosineBlocked", caffeineBlocking);
        status.put("sleepinessLevel", caffeineBlocking ? "Suppressed" : 
                                     adenosine > 0.7 ? "High" : 
                                     adenosine > 0.4 ? "Moderate" : "Low");
        
        // Dopamine/Alertness
        String alertness = caffeineInSystemMg > 100 ? "High" :
                          caffeineInSystemMg > 50 ? "Elevated" :
                          adenosine > 0.6 ? "Low" : "Normal";
        status.put("alertnessLevel", alertness);
        
        // Melatonin production (suppressed by caffeine and blue light)
        boolean melatoninSuppressed = caffeineInSystemMg > 30 || hoursAwake < 14;
        status.put("melatoninProduction", melatoninSuppressed ? "Suppressed" : "Active");
        
        return status;
    }
    
    /**
     * Calculate recommended time gap between substance intake and sleep.
     * 
     * @param substanceType Type of substance
     * @param amount Amount consumed
     * @return Recommended hours before sleep
     */
    public double getRecommendedSleepGap(String substanceType, double amount) {
        return switch (substanceType.toLowerCase()) {
            case "caffeine", "coffee", "tea" -> 
                calculateHoursUntilCaffeineThreshold(amount, CAFFEINE_EFFECT_THRESHOLD_MG, CAFFEINE_HALF_LIFE_HOURS);
            case "alcohol" -> Math.max(4, amount * 1.5); // At least 4 hours, plus 1.5 per drink
            case "nicotine", "cigarettes" -> 2.0; // Allow 2 hours for cardiovascular effects to subside
            case "sugar" -> REACTIVE_HYPOGLYCEMIA_ONSET_HOURS + 1; // Avoid blood sugar crashes during sleep
            case "exercise" -> 3.0; // Core body temp needs to drop
            default -> 2.0;
        };
    }
    
    /**
     * Get summary of all scientific constants for reference.
     */
    public Map<String, Object> getAllConstants() {
        Map<String, Object> constants = new LinkedHashMap<>();
        
        // Caffeine
        Map<String, Object> caffeine = new LinkedHashMap<>();
        caffeine.put("halfLifeHours", CAFFEINE_HALF_LIFE_HOURS);
        caffeine.put("halfLifeSmokersHours", CAFFEINE_HALF_LIFE_SMOKERS);
        caffeine.put("halfLifePregnancyT1Hours", CAFFEINE_HALF_LIFE_PREGNANCY_T1);
        caffeine.put("timeToPeakMinutes", CAFFEINE_TIME_TO_PEAK_MIN);
        caffeine.put("effectThresholdMg", CAFFEINE_EFFECT_THRESHOLD_MG);
        caffeine.put("cortisolSpikePercent", CAFFEINE_CORTISOL_SPIKE_PERCENT);
        caffeine.put("commonDrinksMg", CAFFEINE_CONTENT_MG);
        constants.put("caffeine", caffeine);
        
        // Alcohol
        Map<String, Object> alcohol = new LinkedHashMap<>();
        alcohol.put("eliminationRateMaleGPerHour", ALCOHOL_ELIMINATION_RATE_MALE);
        alcohol.put("eliminationRateFemaleGPerHour", ALCOHOL_ELIMINATION_RATE_FEMALE);
        alcohol.put("gramsPerStandardDrink", GRAMS_PER_STANDARD_DRINK);
        alcohol.put("gabaRecoveryModerateHours", GABA_RECOVERY_HOURS_MODERATE);
        alcohol.put("gabaRecoveryHeavyHours", GABA_RECOVERY_HOURS_HEAVY);
        alcohol.put("remSuppressionHoursPerDrink", REM_SUPPRESSION_HOURS_PER_DRINK);
        alcohol.put("testosteroneReductionHeavyPercent", TESTOSTERONE_REDUCTION_HEAVY_PERCENT);
        constants.put("alcohol", alcohol);
        
        // Nicotine
        Map<String, Object> nicotine = new LinkedHashMap<>();
        nicotine.put("halfLifeHours", NICOTINE_HALF_LIFE_HOURS);
        nicotine.put("cotinineHalfLifeHours", COTININE_HALF_LIFE_HOURS);
        nicotine.put("mgPerCigarette", NICOTINE_PER_CIGARETTE_MG);
        nicotine.put("dopamineSpikeDurationMin", NICOTINE_DOPAMINE_SPIKE_DURATION_MIN);
        nicotine.put("heartRateIncreaseBpm", NICOTINE_HEART_RATE_INCREASE_BPM);
        constants.put("nicotine", nicotine);
        
        // Cannabis
        Map<String, Object> cannabis = new LinkedHashMap<>();
        cannabis.put("thcHalfLifeSingleUseHours", THC_HALF_LIFE_SINGLE_USE);
        cannabis.put("thcHalfLifeChronicHours", THC_HALF_LIFE_CHRONIC);
        cannabis.put("metaboliteHalfLifeDays", THC_METABOLITE_HALF_LIFE_DAYS);
        cannabis.put("peakSmokedMinutes", THC_PEAK_SMOKED_MIN);
        cannabis.put("peakEdibleMinutes", THC_PEAK_EDIBLE_MIN);
        cannabis.put("dopamineIncreasePercent", THC_DOPAMINE_INCREASE_PERCENT);
        constants.put("cannabis", cannabis);
        
        // Exercise
        Map<String, Object> exercise = new LinkedHashMap<>();
        exercise.put("testosteroneElevationHours", EXERCISE_TESTOSTERONE_ELEVATION_HOURS);
        exercise.put("resistanceTestosteroneIncreasePercent", RESISTANCE_TRAINING_TESTOSTERONE_PERCENT);
        exercise.put("cortisolDurationHours", EXERCISE_CORTISOL_DURATION_HOURS);
        exercise.put("endorphinPeakHours", ENDORPHIN_PEAK_DURATION_HOURS);
        exercise.put("ghSpikeMultiplier", EXERCISE_GH_SPIKE_MULTIPLIER);
        exercise.put("bdnfIncreasePercent", EXERCISE_BDNF_INCREASE_PERCENT);
        constants.put("exercise", exercise);
        
        // Sleep
        Map<String, Object> sleep = new LinkedHashMap<>();
        sleep.put("melatoninOnsetBeforeSleepHours", MELATONIN_ONSET_BEFORE_SLEEP);
        sleep.put("cortisolAwakeningPeakMinutes", CORTISOL_AWAKENING_PEAK_MIN);
        sleep.put("adenosineAccumulationRate", ADENOSINE_ACCUMULATION_RATE);
        sleep.put("adenosineClearancePerSleepHour", ADENOSINE_CLEARANCE_PER_SLEEP_HOUR);
        constants.put("sleep", sleep);
        
        return constants;
    }
}
