package com.health.entity;

import jakarta.persistence.*;

/**
 * Food log with detailed macronutrient tracking.
 * Enhanced for USDA API integration.
 */
@Entity
@Table(name = "food_logs")
public class FoodLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String foodName;
    
    private double grams;
    private int calories;
    private int protein;
    private int fat;
    private int carbohydrates;
    
    // Extended micronutrients (for USDA API integration)
    private Double fiber;
    private Double sugar;
    private Double sodium;
    private Double potassium;
    private Double calcium;
    private Double iron;
    private Double magnesium;
    private Double zinc;
    private Double vitaminA;
    private Double vitaminC;
    private Double vitaminD;
    private Double vitaminB12;
    
    // USDA API reference
    private String fdcId; // USDA Food Data Central ID
    
    @Column(nullable = false)
    private String date;
    
    private String mealType; // "Breakfast", "Lunch", "Dinner", "Snack"
    
    // Constructors
    public FoodLog() {}
    
    public FoodLog(Long userId, String foodName, double grams, int calories, 
                   int protein, int fat, String date) {
        this.userId = userId;
        this.foodName = foodName;
        this.grams = grams;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.date = date;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    
    public double getGrams() { return grams; }
    public void setGrams(double grams) { this.grams = grams; }
    
    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }
    
    public int getProtein() { return protein; }
    public void setProtein(int protein) { this.protein = protein; }
    
    public int getFat() { return fat; }
    public void setFat(int fat) { this.fat = fat; }
    
    public int getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(int carbs) { this.carbohydrates = carbs; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    
    public String getFdcId() { return fdcId; }
    public void setFdcId(String fdcId) { this.fdcId = fdcId; }
    
    // Micronutrient getters/setters
    public Double getFiber() { return fiber; }
    public void setFiber(Double fiber) { this.fiber = fiber; }
    
    public Double getSugar() { return sugar; }
    public void setSugar(Double sugar) { this.sugar = sugar; }
    
    public Double getSodium() { return sodium; }
    public void setSodium(Double sodium) { this.sodium = sodium; }
    
    public Double getPotassium() { return potassium; }
    public void setPotassium(Double potassium) { this.potassium = potassium; }
    
    public Double getCalcite() { return calcium; }
    public void setCalcium(Double calcium) { this.calcium = calcium; }
    
    public Double getIron() { return iron; }
    public void setIron(Double iron) { this.iron = iron; }
    
    public Double getMagnesium() { return magnesium; }
    public void setMagnesium(Double magnesium) { this.magnesium = magnesium; }
    
    public Double getZinc() { return zinc; }
    public void setZinc(Double zinc) { this.zinc = zinc; }
    
    public Double getVitaminA() { return vitaminA; }
    public void setVitaminA(Double vitaminA) { this.vitaminA = vitaminA; }
    
    public Double getVitaminC() { return vitaminC; }
    public void setVitaminC(Double vitaminC) { this.vitaminC = vitaminC; }
    
    public Double getVitaminD() { return vitaminD; }
    public void setVitaminD(Double vitaminD) { this.vitaminD = vitaminD; }
    
    public Double getVitaminB12() { return vitaminB12; }
    public void setVitaminB12(Double vitaminB12) { this.vitaminB12 = vitaminB12; }
}
