package com.health.controller;

import com.health.entity.BiologicalProfile;
import com.health.entity.GenomicMarker;
import com.health.entity.User;
import com.health.service.GenomicDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for Genomic Data Processing - Bio-Vault API
 * 
 * Endpoints for:
 * - Uploading raw DNA files
 * - Retrieving biological profiles
 * - Getting personalized recommendations
 * - Fetching genetic markers by category
 */
@RestController
@RequestMapping("/api/genomics")
public class GenomicController extends BaseController {
    
    @Autowired
    private GenomicDataService genomicService;
    
    /**
     * Upload and process a raw DNA file
     * Accepts .txt files from 23andMe, AncestryDNA, etc.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDnaFile(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) {
        
        User user = getAuthenticatedUser(request);
        
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "EMPTY_FILE",
                    "message", "Please upload a DNA file"
                ));
            }
            
            String filename = file.getOriginalFilename();
            if (filename != null && !filename.toLowerCase().endsWith(".txt")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "INVALID_FORMAT",
                    "message", "Please upload a .txt DNA file"
                ));
            }
            
            // Process the file
            BiologicalProfile profile = genomicService.processGenomicFile(user.getId(), file);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "DNA file processed successfully",
                "profile", formatProfileResponse(profile),
                "snpsAnalyzed", profile.getSnpsAnalyzed(),
                "dataSource", profile.getDataSource()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "PROCESSING_ERROR",
                "message", "Failed to process DNA file: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get the user's biological profile (Bio-Blueprint)
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        
        Optional<BiologicalProfile> profile = genomicService.getProfile(user.getId());
        
        if (profile.isPresent()) {
            return ResponseEntity.ok(formatProfileResponse(profile.get()));
        } else {
            return ResponseEntity.ok(Map.of(
                "hasProfile", false,
                "message", "No genomic data uploaded yet"
            ));
        }
    }
    
    /**
     * Get the efficiency cap multiplier for gamification
     */
    @GetMapping("/efficiency-cap")
    public ResponseEntity<?> getEfficiencyCap(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        
        return genomicService.getProfile(user.getId())
            .map(profile -> ResponseEntity.ok(Map.of(
                "efficiencyCap", profile.getEfficiencyCapMultiplier(),
                "chronotype", profile.getChronotype().name(),
                "recoverySpeed", profile.getRecoverySpeed().name()
            )))
            .orElseGet(() -> ResponseEntity.ok(Map.of(
                "efficiencyCap", 1.05, // Default
                "chronotype", "UNKNOWN",
                "message", "No genomic profile - using default efficiency cap"
            )));
    }
    
    /**
     * Get optimal timing windows based on chronotype
     */
    @GetMapping("/optimal-windows")
    public ResponseEntity<?> getOptimalWindows(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        
        return genomicService.getProfile(user.getId())
            .map(profile -> ResponseEntity.ok(Map.of(
                "chronotype", profile.getChronotype().name(),
                "sleep", Map.of(
                    "optimalWakeTime", profile.getOptimalWakeTime(),
                    "optimalSleepTime", profile.getOptimalSleepTime()
                ),
                "cognitive", Map.of(
                    "peakStart", profile.getPeakCognitiveStart(),
                    "peakEnd", profile.getPeakCognitiveEnd()
                ),
                "physical", Map.of(
                    "peakStart", profile.getPeakPhysicalStart(),
                    "peakEnd", profile.getPeakPhysicalEnd()
                )
            )))
            .orElseGet(() -> ResponseEntity.ok(Map.of(
                "hasProfile", false,
                "message", "Upload DNA file to get personalized timing windows"
            )));
    }
    
    /**
     * Get personalized nutrient recommendations
     */
    @GetMapping("/nutrients")
    public ResponseEntity<?> getNutrientRecommendations(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        
        Map<String, Object> recommendations = genomicService.getNutrientRecommendations(user.getId());
        
        if (recommendations.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "hasProfile", false,
                "message", "Upload DNA file to get personalized nutrient recommendations"
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "hasProfile", true,
            "recommendations", recommendations
        ));
    }
    
    /**
     * Get all genetic markers for the user
     */
    @GetMapping("/markers")
    public ResponseEntity<?> getMarkers(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        
        List<GenomicMarker> markers = genomicService.getMarkers(user.getId());
        
        return ResponseEntity.ok(Map.of(
            "count", markers.size(),
            "markers", markers.stream()
                .map(this::formatMarkerResponse)
                .collect(Collectors.toList())
        ));
    }
    
    /**
     * Get markers by category
     */
    @GetMapping("/markers/{category}")
    public ResponseEntity<?> getMarkersByCategory(
            HttpServletRequest request,
            @PathVariable String category) {
        
        User user = getAuthenticatedUser(request);
        
        try {
            GenomicMarker.MarkerCategory cat = GenomicMarker.MarkerCategory.valueOf(category.toUpperCase());
            List<GenomicMarker> markers = genomicService.getMarkersByCategory(user.getId(), cat);
            
            return ResponseEntity.ok(Map.of(
                "category", category,
                "count", markers.size(),
                "markers", markers.stream()
                    .map(this::formatMarkerResponse)
                    .collect(Collectors.toList())
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "INVALID_CATEGORY",
                "validCategories", List.of("CIRCADIAN", "NUTRIENT_METABOLISM", "PHYSICAL", "RECOVERY", "METABOLIC")
            ));
        }
    }
    
    /**
     * Check if user has genomic data
     */
    @GetMapping("/status")
    public ResponseEntity<?> getStatus(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        
        boolean hasProfile = genomicService.hasProfile(user.getId());
        
        if (hasProfile) {
            BiologicalProfile profile = genomicService.getProfile(user.getId()).get();
            return ResponseEntity.ok(Map.of(
                "hasProfile", true,
                "uploadedAt", profile.getUploadedAt().toString(),
                "dataSource", profile.getDataSource(),
                "snpsAnalyzed", profile.getSnpsAnalyzed()
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "hasProfile", false
        ));
    }
    
    /**
     * Delete user's genomic data
     */
    @DeleteMapping("/data")
    public ResponseEntity<?> deleteData(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        
        genomicService.deleteUserData(user.getId());
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Genomic data deleted"
        ));
    }
    
    // ============== RESPONSE FORMATTERS ==============
    
    private Map<String, Object> formatProfileResponse(BiologicalProfile profile) {
        Map<String, Object> response = new LinkedHashMap<>();
        
        response.put("hasProfile", true);
        
        // Chronotype & Timing
        response.put("chronotype", Map.of(
            "type", profile.getChronotype().name(),
            "optimalWakeTime", profile.getOptimalWakeTime(),
            "optimalSleepTime", profile.getOptimalSleepTime(),
            "peakCognitive", Map.of(
                "start", profile.getPeakCognitiveStart(),
                "end", profile.getPeakCognitiveEnd()
            ),
            "peakPhysical", Map.of(
                "start", profile.getPeakPhysicalStart(),
                "end", profile.getPeakPhysicalEnd()
            )
        ));
        
        // Nutrients
        response.put("nutrients", Map.of(
            "vitaminB12", profile.getVitaminB12Status().name(),
            "vitaminD", profile.getVitaminDStatus().name(),
            "folate", profile.getFolateStatus().name(),
            "omega3Conversion", profile.getOmega3Conversion().name(),
            "lactose", profile.getLactoseStatus().name(),
            "caffeine", profile.getCaffeineMetabolism().name()
        ));
        
        // Physical
        response.put("physical", Map.of(
            "muscleType", profile.getMuscleComposition().name(),
            "aerobicPotential", profile.getAerobicPotential().name(),
            "injuryRisk", profile.getInjuryRisk().name(),
            "recoverySpeed", profile.getRecoverySpeed().name()
        ));
        
        // Efficiency cap
        response.put("efficiencyCap", profile.getEfficiencyCapMultiplier());
        
        // Metadata
        response.put("dataSource", profile.getDataSource());
        response.put("snpsAnalyzed", profile.getSnpsAnalyzed());
        response.put("uploadedAt", profile.getUploadedAt() != null ? profile.getUploadedAt().toString() : null);
        
        return response;
    }
    
    private Map<String, Object> formatMarkerResponse(GenomicMarker marker) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("rsid", marker.getRsid());
        response.put("genotype", marker.getGenotype());
        response.put("gene", marker.getGene());
        response.put("category", marker.getCategory().name());
        response.put("significance", marker.getSignificance().name());
        response.put("interpretation", marker.getInterpretation());
        return response;
    }
}
