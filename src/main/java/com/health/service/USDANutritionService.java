package com.health.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/**
 * Service for integrating with USDA FoodData Central API.
 * Provides access to comprehensive nutritional data for over 300,000 foods.
 * 
 * API Documentation: https://fdc.nal.usda.gov/api-guide.html
 */
@Service
public class USDANutritionService {
    
    @Value("${usda.api.key:DEMO_KEY}")
    private String apiKey;
    
    @Value("${usda.api.base-url:https://api.nal.usda.gov/fdc/v1}")
    private String baseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Nutrient IDs from USDA FoodData Central
    private static final int NUTRIENT_ID_ENERGY = 1008;      // Calories (kcal)
    private static final int NUTRIENT_ID_PROTEIN = 1003;     // Protein (g)
    private static final int NUTRIENT_ID_FAT = 1004;         // Total Fat (g)
    private static final int NUTRIENT_ID_CARBS = 1005;       // Carbohydrates (g)
    private static final int NUTRIENT_ID_FIBER = 1079;       // Fiber (g)
    private static final int NUTRIENT_ID_SUGARS = 2000;      // Total Sugars (g)
    private static final int NUTRIENT_ID_SODIUM = 1093;      // Sodium (mg)
    private static final int NUTRIENT_ID_POTASSIUM = 1092;   // Potassium (mg)
    private static final int NUTRIENT_ID_CALCIUM = 1087;     // Calcium (mg)
    private static final int NUTRIENT_ID_IRON = 1089;        // Iron (mg)
    private static final int NUTRIENT_ID_MAGNESIUM = 1090;   // Magnesium (mg)
    private static final int NUTRIENT_ID_VITAMIN_C = 1162;   // Vitamin C (mg)
    private static final int NUTRIENT_ID_VITAMIN_D = 1114;   // Vitamin D (mcg)
    private static final int NUTRIENT_ID_VITAMIN_B12 = 1178; // Vitamin B12 (mcg)
    private static final int NUTRIENT_ID_CHOLESTEROL = 1253; // Cholesterol (mg)
    private static final int NUTRIENT_ID_SAT_FAT = 1258;     // Saturated Fat (g)
    
    /**
     * Search for foods in the USDA database.
     * 
     * @param query Search query (e.g., "chicken breast", "apple")
     * @param pageSize Number of results to return (max 50)
     * @return List of food items with nutritional data
     */
    public List<Map<String, Object>> searchFoods(String query, int pageSize) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            // Simple URL construction without complex dataType filter
            String encodedQuery = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
            String url = baseUrl + "/foods/search?api_key=" + apiKey 
                + "&query=" + encodedQuery
                + "&pageSize=" + Math.min(pageSize, 50);
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode foods = root.get("foods");
            
            if (foods != null && foods.isArray()) {
                for (JsonNode food : foods) {
                    Map<String, Object> foodData = extractFoodData(food);
                    results.add(foodData);
                }
            }
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("USDA API error: " + e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Get detailed nutritional information for a specific food by FDC ID.
     * 
     * @param fdcId The FoodData Central ID
     * @return Detailed nutritional data
     */
    public Map<String, Object> getFoodDetails(String fdcId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/food/" + fdcId)
                    .queryParam("api_key", apiKey)
                    .queryParam("format", "full")
                    .build()
                    .toUriString();
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode food = objectMapper.readTree(response);
            result = extractDetailedFoodData(food);
        } catch (Exception e) {
            System.err.println("USDA API error: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get foods by list of FDC IDs (batch request).
     */
    public List<Map<String, Object>> getFoodsByIds(List<String> fdcIds) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            String url = baseUrl + "/foods?api_key=" + apiKey;
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fdcIds", fdcIds.stream().map(Integer::parseInt).toList());
            
            String response = restTemplate.postForObject(url, requestBody, String.class);
            JsonNode foods = objectMapper.readTree(response);
            
            if (foods != null && foods.isArray()) {
                for (JsonNode food : foods) {
                    results.add(extractDetailedFoodData(food));
                }
            }
        } catch (Exception e) {
            System.err.println("USDA API batch error: " + e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Extract basic food data from search results.
     */
    private Map<String, Object> extractFoodData(JsonNode food) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("fdcId", getTextValue(food, "fdcId"));
        data.put("name", getTextValue(food, "description"));
        data.put("brandOwner", getTextValue(food, "brandOwner"));
        data.put("brandName", getTextValue(food, "brandName"));
        data.put("dataType", getTextValue(food, "dataType"));
        data.put("servingSize", getDoubleValue(food, "servingSize"));
        data.put("servingSizeUnit", getTextValue(food, "servingSizeUnit"));
        data.put("source", "USDA");
        
        // Extract nutrients
        JsonNode nutrients = food.get("foodNutrients");
        if (nutrients != null && nutrients.isArray()) {
            data.putAll(extractNutrients(nutrients));
        }
        
        return data;
    }
    
    /**
     * Extract detailed food data including all micronutrients.
     */
    private Map<String, Object> extractDetailedFoodData(JsonNode food) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("fdcId", getTextValue(food, "fdcId"));
        data.put("name", getTextValue(food, "description"));
        data.put("brandOwner", getTextValue(food, "brandOwner"));
        data.put("dataType", getTextValue(food, "dataType"));
        data.put("publicationDate", getTextValue(food, "publicationDate"));
        data.put("source", "USDA");
        
        // Get serving size info
        JsonNode portions = food.get("foodPortions");
        if (portions != null && portions.isArray() && portions.size() > 0) {
            JsonNode portion = portions.get(0);
            data.put("servingSize", getDoubleValue(portion, "gramWeight"));
            data.put("servingDescription", getTextValue(portion, "portionDescription"));
        }
        
        // Extract all nutrients
        JsonNode nutrients = food.get("foodNutrients");
        if (nutrients != null && nutrients.isArray()) {
            data.putAll(extractDetailedNutrients(nutrients));
        }
        
        return data;
    }
    
    /**
     * Extract key macronutrients from nutrient array.
     */
    private Map<String, Object> extractNutrients(JsonNode nutrients) {
        Map<String, Object> data = new HashMap<>();
        
        // Initialize with zeros
        data.put("calories", 0.0);
        data.put("protein", 0.0);
        data.put("fat", 0.0);
        data.put("carbs", 0.0);
        data.put("fiber", 0.0);
        data.put("sodium", 0.0);
        
        for (JsonNode nutrient : nutrients) {
            int nutrientId = nutrient.has("nutrientId") ? 
                    nutrient.get("nutrientId").asInt() : 0;
            double value = nutrient.has("value") ? 
                    nutrient.get("value").asDouble() : 0.0;
            
            switch (nutrientId) {
                case NUTRIENT_ID_ENERGY -> data.put("calories", value);
                case NUTRIENT_ID_PROTEIN -> data.put("protein", value);
                case NUTRIENT_ID_FAT -> data.put("fat", value);
                case NUTRIENT_ID_CARBS -> data.put("carbs", value);
                case NUTRIENT_ID_FIBER -> data.put("fiber", value);
                case NUTRIENT_ID_SODIUM -> data.put("sodium", value);
            }
        }
        
        return data;
    }
    
    /**
     * Extract all nutrients including micronutrients.
     */
    private Map<String, Object> extractDetailedNutrients(JsonNode nutrients) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> vitamins = new HashMap<>();
        Map<String, Object> minerals = new HashMap<>();
        
        for (JsonNode nutrient : nutrients) {
            // Handle both search result format and detail format
            JsonNode nutrientInfo = nutrient.get("nutrient");
            int nutrientId;
            double value;
            String unit;
            
            if (nutrientInfo != null) {
                // Detail format
                nutrientId = nutrientInfo.has("id") ? nutrientInfo.get("id").asInt() : 0;
                value = nutrient.has("amount") ? nutrient.get("amount").asDouble() : 0.0;
                unit = nutrientInfo.has("unitName") ? nutrientInfo.get("unitName").asText() : "";
            } else {
                // Search format
                nutrientId = nutrient.has("nutrientId") ? nutrient.get("nutrientId").asInt() : 0;
                value = nutrient.has("value") ? nutrient.get("value").asDouble() : 0.0;
                unit = nutrient.has("unitName") ? nutrient.get("unitName").asText() : "";
            }
            
            switch (nutrientId) {
                case NUTRIENT_ID_ENERGY -> data.put("calories", value);
                case NUTRIENT_ID_PROTEIN -> data.put("protein", value);
                case NUTRIENT_ID_FAT -> data.put("fat", value);
                case NUTRIENT_ID_CARBS -> data.put("carbs", value);
                case NUTRIENT_ID_FIBER -> data.put("fiber", value);
                case NUTRIENT_ID_SUGARS -> data.put("sugars", value);
                case NUTRIENT_ID_SAT_FAT -> data.put("saturatedFat", value);
                case NUTRIENT_ID_CHOLESTEROL -> data.put("cholesterol", value);
                case NUTRIENT_ID_SODIUM -> minerals.put("sodium", value);
                case NUTRIENT_ID_POTASSIUM -> minerals.put("potassium", value);
                case NUTRIENT_ID_CALCIUM -> minerals.put("calcium", value);
                case NUTRIENT_ID_IRON -> minerals.put("iron", value);
                case NUTRIENT_ID_MAGNESIUM -> minerals.put("magnesium", value);
                case NUTRIENT_ID_VITAMIN_C -> vitamins.put("vitaminC", value);
                case NUTRIENT_ID_VITAMIN_D -> vitamins.put("vitaminD", value);
                case NUTRIENT_ID_VITAMIN_B12 -> vitamins.put("vitaminB12", value);
            }
        }
        
        data.put("vitamins", vitamins);
        data.put("minerals", minerals);
        
        return data;
    }
    
    private String getTextValue(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? 
                node.get(field).asText() : "";
    }
    
    private double getDoubleValue(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? 
                node.get(field).asDouble() : 0.0;
    }
}
