package com.health.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/**
 * Service for integrating with wger Workout Manager API.
 * Provides access to comprehensive exercise database with muscle groups,
 * equipment requirements, and exercise instructions.
 * 
 * API Documentation: https://wger.de/en/software/api
 * Note: wger API is free and does not require an API key.
 */
@Service
public class WgerExerciseService {
    
    @Value("${wger.api.base-url:https://wger.de/api/v2}")
    private String baseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Muscle groups mapping (from wger API)
    private static final Map<Integer, String> MUSCLE_GROUPS = Map.ofEntries(
            Map.entry(1, "Biceps"),
            Map.entry(2, "Anterior Deltoid"),
            Map.entry(3, "Serratus Anterior"),
            Map.entry(4, "Chest"),
            Map.entry(5, "Triceps"),
            Map.entry(6, "Abs"),
            Map.entry(7, "Gastrocnemius"),
            Map.entry(8, "Glutes"),
            Map.entry(9, "Trapezius"),
            Map.entry(10, "Quadriceps"),
            Map.entry(11, "Hamstrings"),
            Map.entry(12, "Lats"),
            Map.entry(13, "Brachialis"),
            Map.entry(14, "Obliques"),
            Map.entry(15, "Soleus")
    );
    
    // Equipment mapping (from wger API)
    private static final Map<Integer, String> EQUIPMENT = Map.ofEntries(
            Map.entry(1, "Barbell"),
            Map.entry(2, "SZ-Bar"),
            Map.entry(3, "Dumbbell"),
            Map.entry(4, "Gym Mat"),
            Map.entry(5, "Swiss Ball"),
            Map.entry(6, "Pull-up Bar"),
            Map.entry(7, "None (Bodyweight)"),
            Map.entry(8, "Bench"),
            Map.entry(9, "Incline Bench"),
            Map.entry(10, "Kettlebell")
    );
    
    // Category mapping (exercise types)
    private static final Map<Integer, String> CATEGORIES = Map.ofEntries(
            Map.entry(8, "Arms"),
            Map.entry(9, "Legs"),
            Map.entry(10, "Abs"),
            Map.entry(11, "Chest"),
            Map.entry(12, "Back"),
            Map.entry(13, "Shoulders"),
            Map.entry(14, "Calves"),
            Map.entry(15, "Cardio")
    );
    
    // MET values for calorie estimation (Metabolic Equivalent of Task)
    // Source: Compendium of Physical Activities
    private static final Map<String, Double> MET_VALUES = Map.ofEntries(
            Map.entry("Arms", 3.5),
            Map.entry("Legs", 6.0),
            Map.entry("Abs", 3.0),
            Map.entry("Chest", 5.0),
            Map.entry("Back", 5.0),
            Map.entry("Shoulders", 4.0),
            Map.entry("Calves", 4.5),
            Map.entry("Cardio", 7.0),
            Map.entry("General", 4.0)
    );
    
    /**
     * Search for exercises by name.
     * Uses /exercise/search/ endpoint which returns autocomplete suggestions with names.
     * 
     * @param query Search query (exercise name)
     * @param category Category/muscle group filter (optional)
     * @param limit Maximum results to return
     * @return List of matching exercises
     */
    public List<Map<String, Object>> searchExercises(String query, String category, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            // Use exercise/search endpoint for autocomplete with names
            String searchUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/exercise/search/")
                    .queryParam("term", query != null && !query.isEmpty() ? query : "a")
                    .queryParam("language", 2) // English
                    .build()
                    .toUriString();
            
            String response = restTemplate.getForObject(searchUrl, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode suggestions = root.get("suggestions");
            
            if (suggestions != null && suggestions.isArray()) {
                String categoryLower = category != null ? category.toLowerCase() : "";
                
                for (JsonNode suggestion : suggestions) {
                    JsonNode data = suggestion.get("data");
                    if (data == null) continue;
                    
                    Map<String, Object> exerciseData = new HashMap<>();
                    exerciseData.put("id", data.has("id") ? data.get("id").asInt() : 0);
                    exerciseData.put("base_id", data.has("base_id") ? data.get("base_id").asInt() : 0);
                    exerciseData.put("name", data.has("name") ? data.get("name").asText() : "Unknown");
                    exerciseData.put("category", data.has("category") ? data.get("category").asText() : "General");
                    exerciseData.put("source", "wger");
                    
                    // Get image if available
                    if (data.has("image") && !data.get("image").isNull()) {
                        String imagePath = data.get("image").asText();
                        exerciseData.put("image", imagePath.startsWith("http") ? imagePath : "https://wger.de" + imagePath);
                    }
                    
                    // Filter by category if specified
                    String exerciseCategory = (String) exerciseData.get("category");
                    if (!categoryLower.isEmpty() && !exerciseCategory.toLowerCase().contains(categoryLower)) {
                        continue;
                    }
                    
                    // Map category to muscles for display
                    List<String> muscles = new ArrayList<>();
                    String cat = exerciseCategory;
                    if ("Chest".equalsIgnoreCase(cat)) muscles.addAll(List.of("Chest", "Triceps"));
                    else if ("Back".equalsIgnoreCase(cat)) muscles.addAll(List.of("Back", "Biceps"));
                    else if ("Legs".equalsIgnoreCase(cat)) muscles.addAll(List.of("Quads", "Hamstrings"));
                    else if ("Arms".equalsIgnoreCase(cat)) muscles.addAll(List.of("Biceps", "Triceps"));
                    else if ("Shoulders".equalsIgnoreCase(cat)) muscles.add("Shoulders");
                    else if ("Abs".equalsIgnoreCase(cat)) muscles.add("Abs");
                    else if ("Calves".equalsIgnoreCase(cat)) muscles.add("Calves");
                    else if ("Cardio".equalsIgnoreCase(cat)) muscles.add("Cardio");
                    exerciseData.put("muscles", muscles);
                    
                    // MET value based on category
                    exerciseData.put("met", MET_VALUES.getOrDefault(cat, 4.0));
                    
                    results.add(exerciseData);
                    if (results.size() >= limit) break;
                }
            }
        } catch (Exception e) {
            System.err.println("Wger API error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Get detailed exercise information by ID.
     * Uses exerciseinfo endpoint which includes translations with names.
     */
    public Map<String, Object> getExerciseDetails(int exerciseId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String url = baseUrl + "/exerciseinfo/" + exerciseId + "/?language=2";
            String response = restTemplate.getForObject(url, String.class);
            JsonNode exercise = objectMapper.readTree(response);
            result = extractExerciseInfoData(exercise);
        } catch (Exception e) {
            System.err.println("Wger API error: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get all exercises for a specific muscle group.
     * Uses exerciseinfo endpoint for each base to get translated names.
     */
    public List<Map<String, Object>> getExercisesByMuscle(int muscleId, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        String muscleName = MUSCLE_GROUPS.getOrDefault(muscleId, "").toLowerCase();
        
        try {
            // Use search endpoint with muscle name to get exercises with names
            String searchUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/exercise/search/")
                    .queryParam("term", muscleName.isEmpty() ? "exercise" : muscleName)
                    .queryParam("language", 2)
                    .build()
                    .toUriString();
            
            String response = restTemplate.getForObject(searchUrl, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode suggestions = root.get("suggestions");
            
            if (suggestions != null && suggestions.isArray()) {
                for (JsonNode suggestion : suggestions) {
                    JsonNode data = suggestion.get("data");
                    if (data == null) continue;
                    
                    Map<String, Object> exerciseData = new HashMap<>();
                    exerciseData.put("id", data.has("id") ? data.get("id").asInt() : 0);
                    exerciseData.put("base_id", data.has("base_id") ? data.get("base_id").asInt() : 0);
                    exerciseData.put("name", data.has("name") ? data.get("name").asText() : "Unknown");
                    exerciseData.put("category", data.has("category") ? data.get("category").asText() : "General");
                    exerciseData.put("source", "wger");
                    
                    // Get image
                    if (data.has("image") && !data.get("image").isNull()) {
                        String imagePath = data.get("image").asText();
                        exerciseData.put("image", imagePath.startsWith("http") ? imagePath : "https://wger.de" + imagePath);
                    }
                    
                    // Map muscles
                    List<String> muscles = new ArrayList<>();
                    muscles.add(MUSCLE_GROUPS.getOrDefault(muscleId, "General"));
                    exerciseData.put("muscles", muscles);
                    exerciseData.put("met", MET_VALUES.getOrDefault((String) exerciseData.get("category"), 4.0));
                    
                    results.add(exerciseData);
                    if (results.size() >= limit) break;
                }
            }
        } catch (Exception e) {
            System.err.println("Wger API error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Get all muscle groups.
     */
    public List<Map<String, Object>> getMuscleGroups() {
        return MUSCLE_GROUPS.entrySet().stream()
                .map(e -> Map.<String, Object>of(
                        "id", e.getKey(),
                        "name", e.getValue()
                ))
                .toList();
    }
    
    /**
     * Get all equipment types.
     */
    public List<Map<String, Object>> getEquipment() {
        return EQUIPMENT.entrySet().stream()
                .map(e -> Map.<String, Object>of(
                        "id", e.getKey(),
                        "name", e.getValue()
                ))
                .toList();
    }
    
    /**
     * Get exercise categories.
     */
    public List<Map<String, Object>> getCategories() {
        return CATEGORIES.entrySet().stream()
                .map(e -> Map.<String, Object>of(
                        "id", e.getKey(),
                        "name", e.getValue()
                ))
                .toList();
    }
    
    /**
     * Estimate calories burned for an exercise.
     * 
     * @param category Exercise category
     * @param durationMinutes Duration in minutes
     * @param weightKg User's weight in kg
     * @return Estimated calories burned
     */
    public int estimateCaloriesBurned(String category, int durationMinutes, double weightKg) {
        double met = MET_VALUES.getOrDefault(category, MET_VALUES.get("General"));
        // Calorie formula: MET * weight(kg) * time(hours)
        return (int) Math.round(met * weightKg * (durationMinutes / 60.0));
    }
    
    /**
     * Extract exercise data from exercisebaseinfo endpoint response.
     * This endpoint includes translations array with exercise names.
     */
    private Map<String, Object> extractExerciseBaseInfo(JsonNode exercise) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("id", exercise.has("id") ? exercise.get("id").asInt() : 0);
        data.put("source", "wger");
        
        // Get name from exercises array (translations) - prefer English (language 2)
        String name = "";
        String description = "";
        if (exercise.has("exercises") && exercise.get("exercises").isArray()) {
            for (JsonNode translation : exercise.get("exercises")) {
                // Check if this is English (language 2)
                int langId = translation.has("language") ? translation.get("language").asInt() : 0;
                String translatedName = getTextValue(translation, "name");
                String translatedDesc = getTextValue(translation, "description");
                
                if (langId == 2 && !translatedName.isEmpty()) {
                    name = translatedName;
                    description = translatedDesc;
                    break;
                }
                // Fallback to any available name
                if (name.isEmpty() && !translatedName.isEmpty()) {
                    name = translatedName;
                    description = translatedDesc;
                }
            }
        }
        
        // Fallback to direct name field if available
        if (name.isEmpty()) {
            name = getTextValue(exercise, "name");
        }
        
        data.put("name", name.isEmpty() ? "Exercise #" + data.get("id") : name);
        data.put("description", stripHtml(description));
        
        // Extract category
        if (exercise.has("category")) {
            JsonNode catNode = exercise.get("category");
            if (catNode.isObject()) {
                int catId = catNode.has("id") ? catNode.get("id").asInt() : 0;
                String catName = catNode.has("name") ? catNode.get("name").asText() : CATEGORIES.getOrDefault(catId, "General");
                data.put("categoryId", catId);
                data.put("category", catName);
            } else {
                int catId = catNode.asInt();
                data.put("categoryId", catId);
                data.put("category", CATEGORIES.getOrDefault(catId, "General"));
            }
        }
        
        // Extract muscles
        List<String> muscles = new ArrayList<>();
        if (exercise.has("muscles") && exercise.get("muscles").isArray()) {
            for (JsonNode muscle : exercise.get("muscles")) {
                if (muscle.isObject() && muscle.has("name_en")) {
                    muscles.add(muscle.get("name_en").asText());
                } else if (muscle.isObject() && muscle.has("name")) {
                    muscles.add(muscle.get("name").asText());
                } else {
                    int muscleId = muscle.asInt();
                    muscles.add(MUSCLE_GROUPS.getOrDefault(muscleId, "Unknown"));
                }
            }
        }
        data.put("muscles", muscles);
        data.put("muscleGroups", String.join(", ", muscles));
        
        // Extract equipment
        List<String> equipmentList = new ArrayList<>();
        if (exercise.has("equipment") && exercise.get("equipment").isArray()) {
            for (JsonNode equip : exercise.get("equipment")) {
                if (equip.isObject() && equip.has("name")) {
                    equipmentList.add(equip.get("name").asText());
                } else {
                    int equipId = equip.asInt();
                    equipmentList.add(EQUIPMENT.getOrDefault(equipId, "Unknown"));
                }
            }
        }
        data.put("equipment", equipmentList);
        
        // Add images if available
        List<String> images = new ArrayList<>();
        if (exercise.has("images") && exercise.get("images").isArray()) {
            for (JsonNode img : exercise.get("images")) {
                if (img.has("image")) {
                    images.add(img.get("image").asText());
                }
            }
        }
        data.put("images", images);
        
        // Calculate MET value based on category
        String category = (String) data.getOrDefault("category", "General");
        data.put("met", MET_VALUES.getOrDefault(category, 4.0));
        
        return data;
    }
    
    /**
     * Extract exercise data from exerciseinfo endpoint (single exercise detail).
     * This endpoint has translations array with exercise names.
     */
    private Map<String, Object> extractExerciseInfoData(JsonNode exercise) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("id", exercise.has("id") ? exercise.get("id").asInt() : 0);
        data.put("source", "wger");
        
        // Get name from translations array - prefer English (language 2)
        String name = "";
        String description = "";
        if (exercise.has("translations") && exercise.get("translations").isArray()) {
            for (JsonNode translation : exercise.get("translations")) {
                int langId = translation.has("language") ? translation.get("language").asInt() : 0;
                String translatedName = getTextValue(translation, "name");
                String translatedDesc = getTextValue(translation, "description");
                
                if (langId == 2 && !translatedName.isEmpty()) {
                    name = translatedName;
                    description = translatedDesc;
                    break;
                }
                if (name.isEmpty() && !translatedName.isEmpty()) {
                    name = translatedName;
                    description = translatedDesc;
                }
            }
        }
        
        data.put("name", name.isEmpty() ? "Exercise #" + data.get("id") : name);
        data.put("description", stripHtml(description));
        
        // Extract category
        if (exercise.has("category")) {
            JsonNode catNode = exercise.get("category");
            if (catNode.isObject()) {
                data.put("categoryId", catNode.has("id") ? catNode.get("id").asInt() : 0);
                data.put("category", catNode.has("name") ? catNode.get("name").asText() : "General");
            }
        }
        
        // Extract muscles
        List<String> muscles = new ArrayList<>();
        if (exercise.has("muscles") && exercise.get("muscles").isArray()) {
            for (JsonNode muscle : exercise.get("muscles")) {
                if (muscle.isObject() && muscle.has("name_en")) {
                    muscles.add(muscle.get("name_en").asText());
                } else if (muscle.isObject() && muscle.has("name")) {
                    muscles.add(muscle.get("name").asText());
                }
            }
        }
        data.put("muscles", muscles);
        
        // Extract equipment
        List<String> equipmentList = new ArrayList<>();
        if (exercise.has("equipment") && exercise.get("equipment").isArray()) {
            for (JsonNode equip : exercise.get("equipment")) {
                if (equip.isObject() && equip.has("name")) {
                    equipmentList.add(equip.get("name").asText());
                }
            }
        }
        data.put("equipment", equipmentList);
        
        // Extract images
        List<String> images = new ArrayList<>();
        if (exercise.has("images") && exercise.get("images").isArray()) {
            for (JsonNode img : exercise.get("images")) {
                if (img.has("image")) {
                    images.add(img.get("image").asText());
                }
            }
        }
        data.put("images", images);
        
        String category2 = (String) data.getOrDefault("category", "General");
        data.put("met", MET_VALUES.getOrDefault(category2, 4.0));
        
        return data;
    }
    
    /**
     * Extract basic exercise data from API response.
     */
    private Map<String, Object> extractExerciseData(JsonNode exercise) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("id", exercise.has("id") ? exercise.get("id").asInt() : 0);
        data.put("name", getTextValue(exercise, "name"));
        data.put("description", stripHtml(getTextValue(exercise, "description")));
        data.put("source", "wger");
        
        // Extract category
        if (exercise.has("category")) {
            int catId = exercise.get("category").asInt();
            data.put("categoryId", catId);
            data.put("category", CATEGORIES.getOrDefault(catId, "General"));
        }
        
        // Extract muscles
        List<String> muscles = new ArrayList<>();
        if (exercise.has("muscles") && exercise.get("muscles").isArray()) {
            for (JsonNode muscle : exercise.get("muscles")) {
                int muscleId = muscle.asInt();
                muscles.add(MUSCLE_GROUPS.getOrDefault(muscleId, "Unknown"));
            }
        }
        data.put("muscles", muscles);
        data.put("muscleGroups", String.join(", ", muscles));
        
        // Extract equipment
        List<String> equipmentList = new ArrayList<>();
        if (exercise.has("equipment") && exercise.get("equipment").isArray()) {
            for (JsonNode equip : exercise.get("equipment")) {
                int equipId = equip.asInt();
                equipmentList.add(EQUIPMENT.getOrDefault(equipId, "Unknown"));
            }
        }
        data.put("equipment", equipmentList);
        
        return data;
    }
    
    /**
     * Extract detailed exercise data including images.
     */
    private Map<String, Object> extractDetailedExerciseData(JsonNode exercise) {
        Map<String, Object> data = extractExerciseData(exercise);
        
        // Add images
        List<String> images = new ArrayList<>();
        if (exercise.has("images") && exercise.get("images").isArray()) {
            for (JsonNode img : exercise.get("images")) {
                if (img.has("image")) {
                    images.add(img.get("image").asText());
                }
            }
        }
        data.put("images", images);
        
        // Add variations
        if (exercise.has("variations") && exercise.get("variations").isArray()) {
            List<Integer> variations = new ArrayList<>();
            for (JsonNode var : exercise.get("variations")) {
                variations.add(var.asInt());
            }
            data.put("variations", variations);
        }
        
        // Calculate estimated calories for 30 min workout (assuming 70kg)
        String category = (String) data.getOrDefault("category", "General");
        data.put("estimatedCaloriesPer30Min", estimateCaloriesBurned(category, 30, 70));
        
        return data;
    }
    
    private String getTextValue(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? 
                node.get(field).asText() : "";
    }
    
    private String stripHtml(String html) {
        if (html == null || html.isEmpty()) return "";
        return html.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim();
    }
}
