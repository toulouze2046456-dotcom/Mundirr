package com.health.service;

import com.health.entity.BiologicalProfile;
import com.health.entity.BiologicalProfile.*;
import com.health.entity.GenomicMarker;
import com.health.entity.GenomicMarker.MarkerCategory;
import com.health.entity.GenomicMarker.Significance;
import com.health.repository.BiologicalProfileRepository;
import com.health.repository.GenomicMarkerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;

/**
 * GenomicDataService - Universal DNA File Ingestion Pipeline
 * 
 * Parses raw .txt DNA files from 23andMe, AncestryDNA, and other sources.
 * Maps specific RSIDs to biological interpretations and calculates:
 * - Optimal circadian windows
 * - Nutrient requirements
 * - Physical performance markers
 * - Dynamic efficiency cap multiplier
 * 
 * Research-backed markers:
 * - CLOCK rs1801260: Circadian rhythm (sleep timing)
 * - PER3 VNTR: Sleep duration needs
 * - FUT2 rs601338: Vitamin B12 absorption
 * - MTHFR rs1801133: Folate metabolism
 * - CYP1A2 rs762551: Caffeine metabolism
 * - VDR rs1544410: Vitamin D needs
 * - ACTN3 rs1815739: Muscle fiber type
 * - COL1A1 rs1800012: Injury susceptibility
 * - IL6 rs1800795: Recovery inflammation
 */
@Service
public class GenomicDataService {
    
    @Autowired
    private BiologicalProfileRepository profileRepo;
    
    @Autowired
    private GenomicMarkerRepository markerRepo;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // ============== KEY RSIDS FOR INTERPRETATION ==============
    
    /**
     * Master list of RSIDs we analyze with their metadata
     */
    private static final Map<String, MarkerInfo> ANALYZED_MARKERS = new LinkedHashMap<>();
    
    static {
        // CIRCADIAN / SLEEP
        ANALYZED_MARKERS.put("rs1801260", new MarkerInfo("CLOCK", MarkerCategory.CIRCADIAN, 
            "Circadian clock gene - determines morning/evening preference"));
        ANALYZED_MARKERS.put("rs57875989", new MarkerInfo("PER3", MarkerCategory.CIRCADIAN,
            "Sleep duration gene - VNTR variant affecting sleep needs"));
        ANALYZED_MARKERS.put("rs12913832", new MarkerInfo("HERC2", MarkerCategory.CIRCADIAN,
            "Blue light sensitivity affecting melatonin"));
        
        // NUTRIENT METABOLISM
        ANALYZED_MARKERS.put("rs601338", new MarkerInfo("FUT2", MarkerCategory.NUTRIENT_METABOLISM,
            "Secretor status - affects B12 and gut microbiome"));
        ANALYZED_MARKERS.put("rs1801133", new MarkerInfo("MTHFR", MarkerCategory.NUTRIENT_METABOLISM,
            "C677T - folate/methylation metabolism"));
        ANALYZED_MARKERS.put("rs1801131", new MarkerInfo("MTHFR", MarkerCategory.NUTRIENT_METABOLISM,
            "A1298C - secondary folate pathway"));
        ANALYZED_MARKERS.put("rs1544410", new MarkerInfo("VDR", MarkerCategory.NUTRIENT_METABOLISM,
            "Vitamin D receptor - affects D3 needs"));
        ANALYZED_MARKERS.put("rs4988235", new MarkerInfo("MCM6", MarkerCategory.NUTRIENT_METABOLISM,
            "Lactase persistence - dairy tolerance"));
        ANALYZED_MARKERS.put("rs174546", new MarkerInfo("FADS1", MarkerCategory.NUTRIENT_METABOLISM,
            "Omega-3 conversion efficiency"));
        
        // CAFFEINE
        ANALYZED_MARKERS.put("rs762551", new MarkerInfo("CYP1A2", MarkerCategory.METABOLIC,
            "Caffeine metabolism speed"));
        
        // PHYSICAL PERFORMANCE
        ANALYZED_MARKERS.put("rs1815739", new MarkerInfo("ACTN3", MarkerCategory.PHYSICAL,
            "Alpha-actinin-3 - power vs endurance"));
        ANALYZED_MARKERS.put("rs8192678", new MarkerInfo("PPARGC1A", MarkerCategory.PHYSICAL,
            "PGC-1alpha - VO2max potential"));
        ANALYZED_MARKERS.put("rs1800012", new MarkerInfo("COL1A1", MarkerCategory.PHYSICAL,
            "Collagen - injury risk"));
        ANALYZED_MARKERS.put("rs12722", new MarkerInfo("COL5A1", MarkerCategory.PHYSICAL,
            "Collagen V - tendon injury risk"));
        
        // RECOVERY / INFLAMMATION
        ANALYZED_MARKERS.put("rs1800795", new MarkerInfo("IL6", MarkerCategory.RECOVERY,
            "Interleukin-6 - inflammation response"));
        ANALYZED_MARKERS.put("rs1800629", new MarkerInfo("TNF", MarkerCategory.RECOVERY,
            "TNF-alpha - recovery inflammation"));
        ANALYZED_MARKERS.put("rs1800896", new MarkerInfo("IL10", MarkerCategory.RECOVERY,
            "Anti-inflammatory response"));
    }
    
    /**
     * Process an uploaded DNA file and generate the biological profile
     */
    @Transactional
    public BiologicalProfile processGenomicFile(Long userId, MultipartFile file) throws Exception {
        // Delete existing data for this user
        markerRepo.deleteByUserId(userId);
        profileRepo.findByUserId(userId).ifPresent(p -> profileRepo.delete(p));
        
        // Parse the file
        Map<String, String> parsedMarkers = parseRawDnaFile(file);
        
        // Detect data source
        String dataSource = detectDataSource(file.getOriginalFilename(), parsedMarkers.size());
        
        // Store all relevant markers
        List<GenomicMarker> savedMarkers = new ArrayList<>();
        for (Map.Entry<String, String> entry : parsedMarkers.entrySet()) {
            String rsid = entry.getKey().toLowerCase();
            if (ANALYZED_MARKERS.containsKey(rsid)) {
                GenomicMarker marker = new GenomicMarker(userId, rsid, entry.getValue());
                MarkerInfo info = ANALYZED_MARKERS.get(rsid);
                marker.setGene(info.gene);
                marker.setCategory(info.category);
                marker.setInterpretation(info.description);
                
                // Determine significance based on genotype
                interpretMarker(marker);
                savedMarkers.add(markerRepo.save(marker));
            }
        }
        
        // Generate biological profile from markers
        BiologicalProfile profile = generateProfile(userId, savedMarkers);
        profile.setDataSource(dataSource);
        profile.setSnpsAnalyzed(parsedMarkers.size());
        profile.setUploadedAt(LocalDateTime.now());
        
        // Store raw markers JSON for future analysis
        Map<String, Object> rawData = new HashMap<>();
        for (GenomicMarker m : savedMarkers) {
            rawData.put(m.getRsid(), Map.of(
                "genotype", m.getGenotype(),
                "gene", m.getGene(),
                "category", m.getCategory().name()
            ));
        }
        profile.setRawMarkersJson(objectMapper.writeValueAsString(rawData));
        
        return profileRepo.save(profile);
    }
    
    /**
     * Parse raw DNA file (.txt format from 23andMe/Ancestry)
     * Format: rsid \t chromosome \t position \t genotype
     * Or: rsid,chromosome,position,genotype (CSV)
     */
    private Map<String, String> parseRawDnaFile(MultipartFile file) throws Exception {
        Map<String, String> markers = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip comments and headers
                if (line.startsWith("#") || line.startsWith("rsid") || line.isEmpty()) {
                    continue;
                }
                
                String[] parts;
                if (line.contains("\t")) {
                    parts = line.split("\t");
                } else if (line.contains(",")) {
                    parts = line.split(",");
                } else {
                    parts = line.split("\\s+");
                }
                
                if (parts.length >= 4) {
                    String rsid = parts[0].trim().toLowerCase();
                    String genotype = parts[3].trim().toUpperCase();
                    
                    // Validate rsid format
                    if (rsid.startsWith("rs") && !genotype.equals("--") && !genotype.equals("II") && !genotype.equals("DD")) {
                        markers.put(rsid, genotype);
                    }
                } else if (parts.length >= 2) {
                    // Simple rsid genotype format
                    String rsid = parts[0].trim().toLowerCase();
                    String genotype = parts[parts.length - 1].trim().toUpperCase();
                    
                    if (rsid.startsWith("rs") && genotype.length() <= 2) {
                        markers.put(rsid, genotype);
                    }
                }
            }
        }
        
        return markers;
    }
    
    /**
     * Detect the source of the genetic data
     */
    private String detectDataSource(String filename, int snpCount) {
        if (filename != null) {
            String lower = filename.toLowerCase();
            if (lower.contains("23andme")) return "23ANDME";
            if (lower.contains("ancestry")) return "ANCESTRYDNA";
            if (lower.contains("ftdna") || lower.contains("familytree")) return "FAMILYTREEDNA";
            if (lower.contains("myheritage")) return "MYHERITAGE";
        }
        
        // Guess based on SNP count
        if (snpCount > 900000) return "23ANDME_V5";
        if (snpCount > 600000) return "ANCESTRYDNA";
        if (snpCount > 500000) return "23ANDME_V4";
        
        return "UNKNOWN";
    }
    
    /**
     * Interpret a single marker and set its significance
     */
    private void interpretMarker(GenomicMarker marker) {
        String rsid = marker.getRsid();
        String genotype = marker.getGenotype();
        
        switch (rsid) {
            case "rs1801260" -> { // CLOCK
                if (genotype.equals("GG")) {
                    marker.setSignificance(Significance.BENEFICIAL);
                    marker.setInterpretation("Morning chronotype - optimal for early schedules");
                } else if (genotype.equals("AA")) {
                    marker.setSignificance(Significance.ATTENTION_NEEDED);
                    marker.setInterpretation("Evening chronotype - may struggle with early mornings");
                } else {
                    marker.setSignificance(Significance.NEUTRAL);
                    marker.setInterpretation("Intermediate chronotype - flexible scheduling");
                }
            }
            
            case "rs601338" -> { // FUT2
                if (genotype.contains("A")) {
                    marker.setSignificance(Significance.ATTENTION_NEEDED);
                    marker.setInterpretation("Non-secretor status - reduced B12 absorption, consider supplementation");
                } else {
                    marker.setSignificance(Significance.BENEFICIAL);
                    marker.setInterpretation("Secretor status - normal B12 absorption");
                }
            }
            
            case "rs1801133" -> { // MTHFR C677T
                if (genotype.equals("TT")) {
                    marker.setSignificance(Significance.ATTENTION_NEEDED);
                    marker.setInterpretation("Homozygous MTHFR - 70% reduced enzyme activity, use methylfolate");
                } else if (genotype.equals("CT") || genotype.equals("TC")) {
                    marker.setSignificance(Significance.ATTENTION_NEEDED);
                    marker.setInterpretation("Heterozygous MTHFR - 35% reduced activity, methylfolate preferred");
                } else {
                    marker.setSignificance(Significance.BENEFICIAL);
                    marker.setInterpretation("Normal MTHFR function");
                }
            }
            
            case "rs762551" -> { // CYP1A2 Caffeine
                if (genotype.equals("AA")) {
                    marker.setSignificance(Significance.BENEFICIAL);
                    marker.setInterpretation("Fast caffeine metabolizer - can have coffee later in day");
                } else if (genotype.equals("CC")) {
                    marker.setSignificance(Significance.ATTENTION_NEEDED);
                    marker.setInterpretation("Slow caffeine metabolizer - avoid after noon for better sleep");
                } else {
                    marker.setSignificance(Significance.NEUTRAL);
                    marker.setInterpretation("Normal caffeine metabolism");
                }
            }
            
            case "rs4988235" -> { // Lactose
                if (genotype.equals("TT")) {
                    marker.setSignificance(Significance.ATTENTION_NEEDED);
                    marker.setInterpretation("Lactose intolerant - avoid dairy or use lactase");
                } else {
                    marker.setSignificance(Significance.BENEFICIAL);
                    marker.setInterpretation("Lactose tolerant - dairy is well processed");
                }
            }
            
            case "rs1815739" -> { // ACTN3
                if (genotype.equals("CC") || genotype.equals("TT")) {
                    marker.setSignificance(Significance.NEUTRAL);
                    marker.setInterpretation("No alpha-actinin-3 - optimized for endurance activities");
                } else if (genotype.equals("CT") || genotype.equals("TC")) {
                    marker.setSignificance(Significance.BENEFICIAL);
                    marker.setInterpretation("Mixed fiber type - versatile for both power and endurance");
                } else {
                    marker.setSignificance(Significance.BENEFICIAL);
                    marker.setInterpretation("Full alpha-actinin-3 - optimized for power/sprint activities");
                }
            }
            
            case "rs1544410" -> { // VDR
                if (genotype.equals("AA")) {
                    marker.setSignificance(Significance.ATTENTION_NEEDED);
                    marker.setInterpretation("Reduced vitamin D receptor - higher D3 supplementation needed");
                } else {
                    marker.setSignificance(Significance.NEUTRAL);
                    marker.setInterpretation("Normal vitamin D receptor function");
                }
            }
            
            case "rs1800795" -> { // IL6
                if (genotype.equals("GG")) {
                    marker.setSignificance(Significance.BENEFICIAL);
                    marker.setInterpretation("Lower baseline inflammation - faster recovery");
                } else if (genotype.equals("CC")) {
                    marker.setSignificance(Significance.ATTENTION_NEEDED);
                    marker.setInterpretation("Higher inflammation response - prioritize anti-inflammatory foods");
                } else {
                    marker.setSignificance(Significance.NEUTRAL);
                    marker.setInterpretation("Normal inflammatory response");
                }
            }
            
            case "rs1800012" -> { // COL1A1
                if (genotype.contains("T")) {
                    marker.setSignificance(Significance.ATTENTION_NEEDED);
                    marker.setInterpretation("Elevated injury risk - emphasize mobility and warm-ups");
                } else {
                    marker.setSignificance(Significance.BENEFICIAL);
                    marker.setInterpretation("Normal collagen structure - standard injury risk");
                }
            }
            
            case "rs174546" -> { // FADS1
                if (genotype.equals("CC")) {
                    marker.setSignificance(Significance.ATTENTION_NEEDED);
                    marker.setInterpretation("Reduced omega-3 conversion - prioritize EPA/DHA over ALA");
                } else {
                    marker.setSignificance(Significance.NEUTRAL);
                    marker.setInterpretation("Normal omega-3 conversion efficiency");
                }
            }
            
            default -> {
                marker.setSignificance(Significance.NEUTRAL);
            }
        }
    }
    
    /**
     * Generate a complete BiologicalProfile from analyzed markers
     */
    private BiologicalProfile generateProfile(Long userId, List<GenomicMarker> markers) {
        BiologicalProfile profile = new BiologicalProfile(userId);
        
        Map<String, String> markerMap = new HashMap<>();
        for (GenomicMarker m : markers) {
            markerMap.put(m.getRsid(), m.getGenotype());
        }
        
        // ============== CHRONOTYPE ANALYSIS ==============
        String clockGenotype = markerMap.get("rs1801260");
        if (clockGenotype != null) {
            if (clockGenotype.equals("GG")) {
                profile.setChronotype(Chronotype.EARLY_BIRD);
                profile.setOptimalWakeTime("05:30");
                profile.setOptimalSleepTime("21:30");
                profile.setPeakCognitiveStart("08:00");
                profile.setPeakCognitiveEnd("12:00");
                profile.setPeakPhysicalStart("07:00");
                profile.setPeakPhysicalEnd("11:00");
            } else if (clockGenotype.equals("AA")) {
                profile.setChronotype(Chronotype.NIGHT_OWL);
                profile.setOptimalWakeTime("08:00");
                profile.setOptimalSleepTime("00:00");
                profile.setPeakCognitiveStart("16:00");
                profile.setPeakCognitiveEnd("22:00");
                profile.setPeakPhysicalStart("17:00");
                profile.setPeakPhysicalEnd("21:00");
            } else {
                profile.setChronotype(Chronotype.INTERMEDIATE);
                profile.setOptimalWakeTime("07:00");
                profile.setOptimalSleepTime("23:00");
                profile.setPeakCognitiveStart("10:00");
                profile.setPeakCognitiveEnd("14:00");
                profile.setPeakPhysicalStart("09:00");
                profile.setPeakPhysicalEnd("18:00");
            }
        } else {
            // Default to intermediate
            profile.setChronotype(Chronotype.INTERMEDIATE);
            profile.setOptimalWakeTime("07:00");
            profile.setOptimalSleepTime("23:00");
            profile.setPeakCognitiveStart("10:00");
            profile.setPeakCognitiveEnd("14:00");
            profile.setPeakPhysicalStart("09:00");
            profile.setPeakPhysicalEnd("18:00");
        }
        
        // ============== NUTRIENT STATUS ==============
        
        // B12 (FUT2)
        String fut2 = markerMap.get("rs601338");
        if (fut2 != null && fut2.contains("A")) {
            profile.setVitaminB12Status(NutrientStatus.REDUCED);
        } else {
            profile.setVitaminB12Status(NutrientStatus.NORMAL);
        }
        
        // Vitamin D
        String vdr = markerMap.get("rs1544410");
        if (vdr != null && vdr.equals("AA")) {
            profile.setVitaminDStatus(NutrientStatus.REDUCED);
        } else {
            profile.setVitaminDStatus(NutrientStatus.NORMAL);
        }
        
        // Folate (MTHFR)
        String mthfr = markerMap.get("rs1801133");
        if (mthfr != null) {
            if (mthfr.equals("TT")) {
                profile.setFolateStatus(NutrientStatus.SEVERELY_REDUCED);
            } else if (mthfr.contains("T")) {
                profile.setFolateStatus(NutrientStatus.REDUCED);
            } else {
                profile.setFolateStatus(NutrientStatus.NORMAL);
            }
        } else {
            profile.setFolateStatus(NutrientStatus.NORMAL);
        }
        
        // Caffeine
        String cyp1a2 = markerMap.get("rs762551");
        if (cyp1a2 != null) {
            if (cyp1a2.equals("AA")) {
                profile.setCaffeineMetabolism(CaffeineMetabolism.FAST);
            } else if (cyp1a2.equals("CC")) {
                profile.setCaffeineMetabolism(CaffeineMetabolism.SLOW);
            } else {
                profile.setCaffeineMetabolism(CaffeineMetabolism.NORMAL);
            }
        } else {
            profile.setCaffeineMetabolism(CaffeineMetabolism.NORMAL);
        }
        
        // Lactose
        String mcm6 = markerMap.get("rs4988235");
        if (mcm6 != null && mcm6.equals("TT")) {
            profile.setLactoseStatus(LactoseStatus.INTOLERANT);
        } else {
            profile.setLactoseStatus(LactoseStatus.TOLERANT);
        }
        
        // Omega-3
        String fads1 = markerMap.get("rs174546");
        if (fads1 != null && fads1.equals("CC")) {
            profile.setOmega3Conversion(NutrientStatus.REDUCED);
        } else {
            profile.setOmega3Conversion(NutrientStatus.NORMAL);
        }
        
        // ============== PHYSICAL PERFORMANCE ==============
        
        // Muscle type (ACTN3)
        String actn3 = markerMap.get("rs1815739");
        if (actn3 != null) {
            if (actn3.equals("CC") || actn3.equals("TT")) {
                profile.setMuscleComposition(MuscleType.ENDURANCE);
            } else if (actn3.equals("CT") || actn3.equals("TC")) {
                profile.setMuscleComposition(MuscleType.BALANCED);
            } else {
                profile.setMuscleComposition(MuscleType.POWER);
            }
        } else {
            profile.setMuscleComposition(MuscleType.BALANCED);
        }
        
        // Aerobic potential
        String ppargc1a = markerMap.get("rs8192678");
        if (ppargc1a != null && ppargc1a.equals("AA")) {
            profile.setAerobicPotential(AerobicPotential.HIGH);
        } else {
            profile.setAerobicPotential(AerobicPotential.NORMAL);
        }
        
        // Injury risk
        String col1a1 = markerMap.get("rs1800012");
        String col5a1 = markerMap.get("rs12722");
        boolean elevatedRisk = (col1a1 != null && col1a1.contains("T")) || 
                               (col5a1 != null && col5a1.contains("C"));
        profile.setInjuryRisk(elevatedRisk ? InjuryRisk.ELEVATED : InjuryRisk.NORMAL);
        
        // Recovery speed
        String il6 = markerMap.get("rs1800795");
        String tnf = markerMap.get("rs1800629");
        if (il6 != null && il6.equals("GG") && (tnf == null || tnf.equals("GG"))) {
            profile.setRecoverySpeed(RecoverySpeed.FAST);
        } else if ((il6 != null && il6.equals("CC")) || (tnf != null && tnf.contains("A"))) {
            profile.setRecoverySpeed(RecoverySpeed.SLOW);
        } else {
            profile.setRecoverySpeed(RecoverySpeed.NORMAL);
        }
        
        // ============== EFFICIENCY CAP CALCULATION ==============
        // Base is 1.05, adjusted by chronotype alignment and recovery speed
        double efficiencyCap = 1.05;
        
        // Chronotype bonus: aligned schedules get higher cap
        if (profile.getChronotype() == Chronotype.EARLY_BIRD) {
            efficiencyCap += 0.01; // Morning types often have more productive habits
        } else if (profile.getChronotype() == Chronotype.NIGHT_OWL) {
            efficiencyCap -= 0.01; // May struggle with conventional schedules
        }
        
        // Recovery speed modifier
        if (profile.getRecoverySpeed() == RecoverySpeed.FAST) {
            efficiencyCap += 0.01;
        } else if (profile.getRecoverySpeed() == RecoverySpeed.SLOW) {
            efficiencyCap -= 0.01;
        }
        
        // Metabolic efficiency (nutrients)
        int metabolicIssues = 0;
        if (profile.getVitaminB12Status() == NutrientStatus.REDUCED) metabolicIssues++;
        if (profile.getFolateStatus() == NutrientStatus.SEVERELY_REDUCED) metabolicIssues++;
        if (profile.getVitaminDStatus() == NutrientStatus.REDUCED) metabolicIssues++;
        
        efficiencyCap -= metabolicIssues * 0.005;
        
        // Clamp to reasonable range
        profile.setEfficiencyCapMultiplier(Math.max(1.02, Math.min(1.08, efficiencyCap)));
        
        return profile;
    }
    
    /**
     * Get the user's biological profile
     */
    public Optional<BiologicalProfile> getProfile(Long userId) {
        return profileRepo.findByUserId(userId);
    }
    
    /**
     * Get all markers for a user
     */
    public List<GenomicMarker> getMarkers(Long userId) {
        return markerRepo.findByUserId(userId);
    }
    
    /**
     * Get markers by category
     */
    public List<GenomicMarker> getMarkersByCategory(Long userId, MarkerCategory category) {
        return markerRepo.findByUserIdAndCategory(userId, category);
    }
    
    /**
     * Check if user has a biological profile
     */
    public boolean hasProfile(Long userId) {
        return profileRepo.existsByUserId(userId);
    }
    
    /**
     * Delete user's genomic data
     */
    @Transactional
    public void deleteUserData(Long userId) {
        markerRepo.deleteByUserId(userId);
        profileRepo.findByUserId(userId).ifPresent(profileRepo::delete);
    }
    
    /**
     * Generate personalized nutrient recommendations
     */
    public Map<String, Object> getNutrientRecommendations(Long userId) {
        return getProfile(userId).map(profile -> {
            Map<String, Object> recommendations = new LinkedHashMap<>();
            
            // B12
            if (profile.getVitaminB12Status() == NutrientStatus.REDUCED) {
                recommendations.put("vitaminB12", Map.of(
                    "status", "ATTENTION_NEEDED",
                    "recommendation", "Consider 1000-2000mcg methylcobalamin daily",
                    "reason", "FUT2 non-secretor status reduces intestinal B12 absorption"
                ));
            }
            
            // Folate
            if (profile.getFolateStatus() != NutrientStatus.NORMAL) {
                String dose = profile.getFolateStatus() == NutrientStatus.SEVERELY_REDUCED 
                    ? "800-1000mcg" : "400mcg";
                recommendations.put("folate", Map.of(
                    "status", profile.getFolateStatus().name(),
                    "recommendation", "Use methylfolate (5-MTHF) " + dose + " daily, avoid folic acid",
                    "reason", "MTHFR polymorphism reduces conversion of folic acid to active form"
                ));
            }
            
            // Vitamin D
            if (profile.getVitaminDStatus() == NutrientStatus.REDUCED) {
                recommendations.put("vitaminD", Map.of(
                    "status", "ATTENTION_NEEDED",
                    "recommendation", "Consider 4000-5000 IU D3 daily with K2",
                    "reason", "VDR variant reduces vitamin D receptor sensitivity"
                ));
            }
            
            // Omega-3
            if (profile.getOmega3Conversion() == NutrientStatus.REDUCED) {
                recommendations.put("omega3", Map.of(
                    "status", "ATTENTION_NEEDED",
                    "recommendation", "Prioritize EPA/DHA supplements over plant sources (ALA)",
                    "reason", "FADS1 variant reduces conversion of ALA to EPA/DHA"
                ));
            }
            
            // Caffeine timing
            if (profile.getCaffeineMetabolism() == CaffeineMetabolism.SLOW) {
                recommendations.put("caffeine", Map.of(
                    "status", "ATTENTION_NEEDED",
                    "recommendation", "Limit caffeine to before noon for optimal sleep",
                    "reason", "CYP1A2 slow metabolizer - caffeine stays in system longer"
                ));
            }
            
            // Lactose
            if (profile.getLactoseStatus() == LactoseStatus.INTOLERANT) {
                recommendations.put("dairy", Map.of(
                    "status", "INTOLERANT",
                    "recommendation", "Use lactose-free alternatives or take lactase with dairy",
                    "reason", "MCM6 variant indicates lactase non-persistence"
                ));
            }
            
            return recommendations;
        }).orElse(Collections.emptyMap());
    }
    
    /**
     * Helper class for marker metadata
     */
    private static class MarkerInfo {
        final String gene;
        final MarkerCategory category;
        final String description;
        
        MarkerInfo(String gene, MarkerCategory category, String description) {
            this.gene = gene;
            this.category = category;
            this.description = description;
        }
    }
}
