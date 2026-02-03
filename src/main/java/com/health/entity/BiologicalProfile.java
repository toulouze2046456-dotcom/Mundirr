package com.health.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * BiologicalProfile - DNA-Derived Personalization Engine
 * 
 * Stores the user's genomic interpretation results including:
 * - Circadian chronotype (for optimal timing windows)
 * - Nutrient metabolism markers (for personalized supplementation)
 * - Recovery genetics (for training optimization)
 * 
 * This data enables the system to dynamically adjust all recommendations
 * based on individual genetic predispositions.
 */
@Entity
@Table(name = "biological_profiles")
public class BiologicalProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long userId;
    
    // ============== CHRONOTYPE & CIRCADIAN ==============
    
    /**
     * Genetic chronotype: EARLY_BIRD, INTERMEDIATE, or NIGHT_OWL
     * Derived from CLOCK rs1801260 and PER3 VNTR polymorphisms
     */
    @Enumerated(EnumType.STRING)
    private Chronotype chronotype;
    
    /**
     * Optimal wake time based on genetics (24h format, e.g., "06:30")
     */
    private String optimalWakeTime;
    
    /**
     * Optimal sleep time based on genetics (24h format, e.g., "22:30")
     */
    private String optimalSleepTime;
    
    /**
     * Peak cognitive performance window start (24h format)
     */
    private String peakCognitiveStart;
    
    /**
     * Peak cognitive performance window end (24h format)
     */
    private String peakCognitiveEnd;
    
    /**
     * Optimal workout window start (24h format)
     */
    private String peakPhysicalStart;
    
    /**
     * Optimal workout window end (24h format)
     */
    private String peakPhysicalEnd;
    
    // ============== NUTRIENT METABOLISM ==============
    
    /**
     * Vitamin B12 absorption efficiency: NORMAL, REDUCED, SEVERELY_REDUCED
     * Derived from FUT2 rs601338 (non-secretor status)
     */
    @Enumerated(EnumType.STRING)
    private NutrientStatus vitaminB12Status;
    
    /**
     * Vitamin D synthesis efficiency: NORMAL, REDUCED
     * Derived from VDR, GC, and CYP2R1 genes
     */
    @Enumerated(EnumType.STRING)
    private NutrientStatus vitaminDStatus;
    
    /**
     * Folate metabolism: NORMAL, REDUCED (MTHFR mutation)
     * Derived from MTHFR rs1801133 (C677T)
     */
    @Enumerated(EnumType.STRING)
    private NutrientStatus folateStatus;
    
    /**
     * Caffeine metabolism: FAST, NORMAL, SLOW
     * Derived from CYP1A2 rs762551
     */
    @Enumerated(EnumType.STRING)
    private CaffeineMetabolism caffeineMetabolism;
    
    /**
     * Lactose tolerance: TOLERANT, INTOLERANT
     * Derived from MCM6 rs4988235
     */
    @Enumerated(EnumType.STRING)
    private LactoseStatus lactoseStatus;
    
    /**
     * Omega-3 conversion efficiency: NORMAL, REDUCED
     * Derived from FADS1/FADS2 genes
     */
    @Enumerated(EnumType.STRING)
    private NutrientStatus omega3Conversion;
    
    // ============== PHYSICAL PERFORMANCE ==============
    
    /**
     * Muscle fiber composition tendency: ENDURANCE, BALANCED, POWER
     * Derived from ACTN3 rs1815739 (R577X)
     */
    @Enumerated(EnumType.STRING)
    private MuscleType muscleComposition;
    
    /**
     * VO2max potential: HIGH, NORMAL, LIMITED
     * Derived from multiple genes including PPARGC1A
     */
    @Enumerated(EnumType.STRING)
    private AerobicPotential aerobicPotential;
    
    /**
     * Injury risk factor: LOW, NORMAL, ELEVATED
     * Derived from COL1A1 and COL5A1 genes
     */
    @Enumerated(EnumType.STRING)
    private InjuryRisk injuryRisk;
    
    /**
     * Recovery speed: FAST, NORMAL, SLOW
     * Derived from IL6 and TNF genes
     */
    @Enumerated(EnumType.STRING)
    private RecoverySpeed recoverySpeed;
    
    // ============== EFFICIENCY CAP ==============
    
    /**
     * Dynamic efficiency cap multiplier based on chronotype alignment
     * Base is 1.05, adjusted by genetic factors
     * Range: 1.02 (misaligned) to 1.08 (perfectly aligned)
     */
    private Double efficiencyCapMultiplier;
    
    // ============== METADATA ==============
    
    /**
     * When the DNA file was uploaded and processed
     */
    private LocalDateTime uploadedAt;
    
    /**
     * Source of the genetic data: 23ANDME, ANCESTRYDNA, OTHER
     */
    private String dataSource;
    
    /**
     * Number of SNPs successfully parsed from the file
     */
    private Integer snpsAnalyzed;
    
    /**
     * Raw JSON of all interpreted markers for future analysis
     */
    @Column(columnDefinition = "TEXT")
    private String rawMarkersJson;
    
    // ============== ENUMS ==============
    
    public enum Chronotype {
        EARLY_BIRD,      // Morning larks (CLOCK G/G or PER3 5/5)
        INTERMEDIATE,    // Neither extreme
        NIGHT_OWL        // Evening types (CLOCK A/A or PER3 4/4)
    }
    
    public enum NutrientStatus {
        NORMAL,
        REDUCED,
        SEVERELY_REDUCED
    }
    
    public enum CaffeineMetabolism {
        FAST,            // CYP1A2 A/A - can have coffee late
        NORMAL,          // A/C - moderate
        SLOW             // C/C - avoid after noon
    }
    
    public enum LactoseStatus {
        TOLERANT,        // C/C or C/T
        INTOLERANT       // T/T
    }
    
    public enum MuscleType {
        ENDURANCE,       // ACTN3 X/X (no alpha-actinin-3)
        BALANCED,        // R/X
        POWER            // R/R (full alpha-actinin-3)
    }
    
    public enum AerobicPotential {
        HIGH,
        NORMAL,
        LIMITED
    }
    
    public enum InjuryRisk {
        LOW,
        NORMAL,
        ELEVATED
    }
    
    public enum RecoverySpeed {
        FAST,
        NORMAL,
        SLOW
    }
    
    // ============== CONSTRUCTORS ==============
    
    public BiologicalProfile() {}
    
    public BiologicalProfile(Long userId) {
        this.userId = userId;
        this.uploadedAt = LocalDateTime.now();
        this.efficiencyCapMultiplier = 1.05; // Default before genetic analysis
    }
    
    // ============== GETTERS AND SETTERS ==============
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Chronotype getChronotype() { return chronotype; }
    public void setChronotype(Chronotype chronotype) { this.chronotype = chronotype; }
    
    public String getOptimalWakeTime() { return optimalWakeTime; }
    public void setOptimalWakeTime(String optimalWakeTime) { this.optimalWakeTime = optimalWakeTime; }
    
    public String getOptimalSleepTime() { return optimalSleepTime; }
    public void setOptimalSleepTime(String optimalSleepTime) { this.optimalSleepTime = optimalSleepTime; }
    
    public String getPeakCognitiveStart() { return peakCognitiveStart; }
    public void setPeakCognitiveStart(String peakCognitiveStart) { this.peakCognitiveStart = peakCognitiveStart; }
    
    public String getPeakCognitiveEnd() { return peakCognitiveEnd; }
    public void setPeakCognitiveEnd(String peakCognitiveEnd) { this.peakCognitiveEnd = peakCognitiveEnd; }
    
    public String getPeakPhysicalStart() { return peakPhysicalStart; }
    public void setPeakPhysicalStart(String peakPhysicalStart) { this.peakPhysicalStart = peakPhysicalStart; }
    
    public String getPeakPhysicalEnd() { return peakPhysicalEnd; }
    public void setPeakPhysicalEnd(String peakPhysicalEnd) { this.peakPhysicalEnd = peakPhysicalEnd; }
    
    public NutrientStatus getVitaminB12Status() { return vitaminB12Status; }
    public void setVitaminB12Status(NutrientStatus vitaminB12Status) { this.vitaminB12Status = vitaminB12Status; }
    
    public NutrientStatus getVitaminDStatus() { return vitaminDStatus; }
    public void setVitaminDStatus(NutrientStatus vitaminDStatus) { this.vitaminDStatus = vitaminDStatus; }
    
    public NutrientStatus getFolateStatus() { return folateStatus; }
    public void setFolateStatus(NutrientStatus folateStatus) { this.folateStatus = folateStatus; }
    
    public CaffeineMetabolism getCaffeineMetabolism() { return caffeineMetabolism; }
    public void setCaffeineMetabolism(CaffeineMetabolism caffeineMetabolism) { this.caffeineMetabolism = caffeineMetabolism; }
    
    public LactoseStatus getLactoseStatus() { return lactoseStatus; }
    public void setLactoseStatus(LactoseStatus lactoseStatus) { this.lactoseStatus = lactoseStatus; }
    
    public NutrientStatus getOmega3Conversion() { return omega3Conversion; }
    public void setOmega3Conversion(NutrientStatus omega3Conversion) { this.omega3Conversion = omega3Conversion; }
    
    public MuscleType getMuscleComposition() { return muscleComposition; }
    public void setMuscleComposition(MuscleType muscleComposition) { this.muscleComposition = muscleComposition; }
    
    public AerobicPotential getAerobicPotential() { return aerobicPotential; }
    public void setAerobicPotential(AerobicPotential aerobicPotential) { this.aerobicPotential = aerobicPotential; }
    
    public InjuryRisk getInjuryRisk() { return injuryRisk; }
    public void setInjuryRisk(InjuryRisk injuryRisk) { this.injuryRisk = injuryRisk; }
    
    public RecoverySpeed getRecoverySpeed() { return recoverySpeed; }
    public void setRecoverySpeed(RecoverySpeed recoverySpeed) { this.recoverySpeed = recoverySpeed; }
    
    public Double getEfficiencyCapMultiplier() { return efficiencyCapMultiplier; }
    public void setEfficiencyCapMultiplier(Double efficiencyCapMultiplier) { this.efficiencyCapMultiplier = efficiencyCapMultiplier; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    
    public Integer getSnpsAnalyzed() { return snpsAnalyzed; }
    public void setSnpsAnalyzed(Integer snpsAnalyzed) { this.snpsAnalyzed = snpsAnalyzed; }
    
    public String getRawMarkersJson() { return rawMarkersJson; }
    public void setRawMarkersJson(String rawMarkersJson) { this.rawMarkersJson = rawMarkersJson; }
}
