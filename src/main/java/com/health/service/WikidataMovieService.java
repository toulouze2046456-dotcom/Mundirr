package com.health.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/**
 * Service for integrating with Wikidata API for movie search.
 * Uses public Wikidata API - no API key required.
 * 
 * API Documentation: https://www.wikidata.org/w/api.php
 * Film entity type: Q11424
 */
@Service
public class WikidataMovieService {
    
    private static final String WIKIDATA_API = "https://www.wikidata.org/w/api.php";
    private static final String FILM_TYPE = "Q11424"; // Wikidata entity for "film"
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Wikidata property IDs
    private static final String P_DIRECTOR = "P57";
    private static final String P_PUBLICATION_DATE = "P577";
    private static final String P_IMAGE = "P18";
    private static final String P_GENRE = "P136";
    private static final String P_DURATION = "P2047";
    private static final String P_INSTANCE_OF = "P31";
    
    /**
     * Search for movies using Wikidata wbsearchentities.
     * Filters results to ensure they are films (Q11424).
     * 
     * @param query Search query (movie title)
     * @param limit Maximum results to return
     * @return List of matching movies with metadata
     */
    public List<Map<String, Object>> searchMovies(String query, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            // Step 1: Search for entities matching the query
            String searchUrl = UriComponentsBuilder.fromHttpUrl(WIKIDATA_API)
                    .queryParam("action", "wbsearchentities")
                    .queryParam("search", query)
                    .queryParam("language", "en")
                    .queryParam("format", "json")
                    .queryParam("limit", Math.min(limit * 3, 50)) // Get extra for filtering
                    .queryParam("type", "item")
                    .build()
                    .toUriString();
            
            String response = restTemplate.getForObject(searchUrl, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode searchResults = root.get("search");
            
            if (searchResults != null && searchResults.isArray()) {
                List<String> entityIds = new ArrayList<>();
                
                for (JsonNode result : searchResults) {
                    String entityId = result.get("id").asText();
                    entityIds.add(entityId);
                    if (entityIds.size() >= limit * 2) break;
                }
                
                // Step 2: Fetch detailed data for each entity
                for (String entityId : entityIds) {
                    Map<String, Object> movieData = getMovieDetails(entityId);
                    
                    // Only add if it's confirmed to be a film
                    if (movieData != null && !movieData.isEmpty() && 
                        Boolean.TRUE.equals(movieData.get("isFilm"))) {
                        results.add(movieData);
                        if (results.size() >= limit) break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Wikidata API error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Get detailed movie information by Wikidata entity ID.
     * 
     * @param entityId Wikidata entity ID (e.g., "Q12345")
     * @return Movie metadata including title, director, year, poster
     */
    public Map<String, Object> getMovieDetails(String entityId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String url = UriComponentsBuilder.fromHttpUrl(WIKIDATA_API)
                    .queryParam("action", "wbgetentities")
                    .queryParam("ids", entityId)
                    .queryParam("format", "json")
                    .queryParam("languages", "en")
                    .queryParam("props", "labels|claims|descriptions")
                    .build()
                    .toUriString();
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode entities = root.get("entities");
            
            if (entities != null && entities.has(entityId)) {
                JsonNode entity = entities.get(entityId);
                
                // Check if this is a film (instance of Q11424)
                boolean isFilm = isInstanceOf(entity, FILM_TYPE);
                result.put("isFilm", isFilm);
                
                if (!isFilm) {
                    return result; // Return early if not a film
                }
                
                result.put("wikidataId", entityId);
                result.put("source", "Wikidata");
                
                // Get title (label)
                if (entity.has("labels") && entity.get("labels").has("en")) {
                    result.put("title", entity.get("labels").get("en").get("value").asText());
                }
                
                // Get description
                if (entity.has("descriptions") && entity.get("descriptions").has("en")) {
                    result.put("description", entity.get("descriptions").get("en").get("value").asText());
                }
                
                JsonNode claims = entity.get("claims");
                if (claims != null) {
                    // Get director (P57)
                    String directorId = getClaimEntityId(claims, P_DIRECTOR);
                    if (directorId != null) {
                        String directorName = getEntityLabel(directorId);
                        result.put("director", directorName);
                        result.put("directorId", directorId);
                    }
                    
                    // Get release year (P577)
                    String releaseDate = getClaimTimeValue(claims, P_PUBLICATION_DATE);
                    if (releaseDate != null) {
                        result.put("releaseDate", releaseDate);
                        // Extract just the year
                        if (releaseDate.length() >= 4) {
                            try {
                                result.put("year", Integer.parseInt(releaseDate.substring(0, 4).replace("+", "")));
                            } catch (NumberFormatException e) {
                                // Ignore parse errors
                            }
                        }
                    }
                    
                    // Get poster/image (P18)
                    String imageName = getClaimStringValue(claims, P_IMAGE);
                    if (imageName != null) {
                        String imageUrl = resolveWikimediaImageUrl(imageName);
                        result.put("posterUrl", imageUrl);
                        result.put("posterFilename", imageName);
                    }
                    
                    // Get genre (P136)
                    String genreId = getClaimEntityId(claims, P_GENRE);
                    if (genreId != null) {
                        String genreName = getEntityLabel(genreId);
                        result.put("genre", genreName);
                    }
                    
                    // Get duration (P2047) in minutes
                    String duration = getClaimQuantityValue(claims, P_DURATION);
                    if (duration != null) {
                        try {
                            result.put("duration", Integer.parseInt(duration));
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Wikidata API error for entity " + entityId + ": " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Check if entity is an instance of a specific type.
     */
    private boolean isInstanceOf(JsonNode entity, String typeId) {
        try {
            JsonNode claims = entity.get("claims");
            if (claims != null && claims.has(P_INSTANCE_OF)) {
                JsonNode instanceClaims = claims.get(P_INSTANCE_OF);
                for (JsonNode claim : instanceClaims) {
                    JsonNode mainsnak = claim.get("mainsnak");
                    if (mainsnak != null && mainsnak.has("datavalue")) {
                        JsonNode datavalue = mainsnak.get("datavalue");
                        if (datavalue.has("value") && datavalue.get("value").has("id")) {
                            String id = datavalue.get("value").get("id").asText();
                            if (typeId.equals(id)) {
                                return true;
                            }
                            // Also check for related film types
                            if (id.equals("Q24869") || // Feature film
                                id.equals("Q202866") || // Animated film
                                id.equals("Q2431196") || // Biographical film
                                id.equals("Q130232")) { // Drama film
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return false;
    }
    
    /**
     * Get entity ID from a claim.
     */
    private String getClaimEntityId(JsonNode claims, String property) {
        try {
            if (claims.has(property)) {
                JsonNode propertyClaims = claims.get(property);
                if (propertyClaims.isArray() && propertyClaims.size() > 0) {
                    JsonNode mainsnak = propertyClaims.get(0).get("mainsnak");
                    if (mainsnak != null && mainsnak.has("datavalue")) {
                        JsonNode datavalue = mainsnak.get("datavalue");
                        if (datavalue.has("value") && datavalue.get("value").has("id")) {
                            return datavalue.get("value").get("id").asText();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
    
    /**
     * Get string value from a claim.
     */
    private String getClaimStringValue(JsonNode claims, String property) {
        try {
            if (claims.has(property)) {
                JsonNode propertyClaims = claims.get(property);
                if (propertyClaims.isArray() && propertyClaims.size() > 0) {
                    JsonNode mainsnak = propertyClaims.get(0).get("mainsnak");
                    if (mainsnak != null && mainsnak.has("datavalue")) {
                        JsonNode datavalue = mainsnak.get("datavalue");
                        if (datavalue.has("value")) {
                            return datavalue.get("value").asText();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
    
    /**
     * Get time value from a claim.
     */
    private String getClaimTimeValue(JsonNode claims, String property) {
        try {
            if (claims.has(property)) {
                JsonNode propertyClaims = claims.get(property);
                if (propertyClaims.isArray() && propertyClaims.size() > 0) {
                    JsonNode mainsnak = propertyClaims.get(0).get("mainsnak");
                    if (mainsnak != null && mainsnak.has("datavalue")) {
                        JsonNode datavalue = mainsnak.get("datavalue");
                        if (datavalue.has("value") && datavalue.get("value").has("time")) {
                            return datavalue.get("value").get("time").asText();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
    
    /**
     * Get quantity value from a claim.
     */
    private String getClaimQuantityValue(JsonNode claims, String property) {
        try {
            if (claims.has(property)) {
                JsonNode propertyClaims = claims.get(property);
                if (propertyClaims.isArray() && propertyClaims.size() > 0) {
                    JsonNode mainsnak = propertyClaims.get(0).get("mainsnak");
                    if (mainsnak != null && mainsnak.has("datavalue")) {
                        JsonNode datavalue = mainsnak.get("datavalue");
                        if (datavalue.has("value") && datavalue.get("value").has("amount")) {
                            String amount = datavalue.get("value").get("amount").asText();
                            // Remove leading + sign
                            return amount.startsWith("+") ? amount.substring(1) : amount;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
    
    /**
     * Get label for an entity ID.
     */
    private String getEntityLabel(String entityId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(WIKIDATA_API)
                    .queryParam("action", "wbgetentities")
                    .queryParam("ids", entityId)
                    .queryParam("format", "json")
                    .queryParam("languages", "en")
                    .queryParam("props", "labels")
                    .build()
                    .toUriString();
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode entities = root.get("entities");
            
            if (entities != null && entities.has(entityId)) {
                JsonNode entity = entities.get(entityId);
                if (entity.has("labels") && entity.get("labels").has("en")) {
                    return entity.get("labels").get("en").get("value").asText();
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
    
    /**
     * Resolve Wikimedia Commons image filename to a usable URL.
     * Uses the standard Wikimedia URL pattern.
     * 
     * @param filename The image filename from Wikidata
     * @return Full URL to the image
     */
    private String resolveWikimediaImageUrl(String filename) {
        if (filename == null || filename.isEmpty()) return null;
        
        try {
            // Replace spaces with underscores
            String normalizedName = filename.replace(" ", "_");
            
            // Calculate MD5 hash for directory structure
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(normalizedName.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append("%02x".formatted(b));
            }
            String hash = sb.toString();
            
            // Wikimedia uses first 2 chars of hash as directory structure
            String a = hash.substring(0, 1);
            String ab = hash.substring(0, 2);
            
            // URL encode the filename
            String encodedName = java.net.URLEncoder.encode(normalizedName, "UTF-8")
                    .replace("+", "_");
            
            // Return the thumbnail URL (300px width for movie posters)
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/" +
                    a + "/" + ab + "/" + encodedName + "/300px-" + encodedName;
        } catch (Exception e) {
            System.err.println("Error resolving Wikimedia URL: " + e.getMessage());
            return null;
        }
    }
}
