package com.health.entity;

import jakarta.persistence.*;

/**
 * GenomicMarker - Individual SNP Storage
 * 
 * Stores individual genetic markers (SNPs) parsed from DNA files.
 * Each marker is associated with a user and contains the raw genotype data.
 */
@Entity
@Table(name = "genomic_markers", 
       indexes = {
           @Index(name = "idx_marker_user_rsid", columnList = "userId, rsid"),
           @Index(name = "idx_marker_rsid", columnList = "rsid")
       })
public class GenomicMarker {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    /**
     * The RSID identifier (e.g., "rs1801260" for CLOCK gene)
     */
    @Column(nullable = false, length = 20)
    private String rsid;
    
    /**
     * The user's genotype (e.g., "AG", "CC", "TT")
     */
    @Column(nullable = false, length = 10)
    private String genotype;
    
    /**
     * Chromosome number (1-22, X, Y, MT)
     */
    @Column(length = 5)
    private String chromosome;
    
    /**
     * Position on chromosome
     */
    private Long position;
    
    /**
     * Gene associated with this SNP (if known)
     */
    @Column(length = 50)
    private String gene;
    
    /**
     * Category of this marker for UI grouping
     */
    @Enumerated(EnumType.STRING)
    private MarkerCategory category;
    
    /**
     * Interpretation significance: BENEFICIAL, NEUTRAL, ATTENTION_NEEDED
     */
    @Enumerated(EnumType.STRING)
    private Significance significance;
    
    /**
     * Human-readable interpretation of this specific genotype
     */
    @Column(columnDefinition = "TEXT")
    private String interpretation;
    
    // ============== ENUMS ==============
    
    public enum MarkerCategory {
        CIRCADIAN,           // Sleep/wake cycle genes
        NUTRIENT_METABOLISM, // Vitamin absorption genes
        PHYSICAL,            // Athletic performance genes
        RECOVERY,            // Inflammation/recovery genes
        COGNITIVE,           // Brain function genes
        METABOLIC,           // Energy metabolism genes
        OTHER
    }
    
    public enum Significance {
        BENEFICIAL,      // Favorable variant
        NEUTRAL,         // Typical/normal
        ATTENTION_NEEDED // May need lifestyle adjustments
    }
    
    // ============== CONSTRUCTORS ==============
    
    public GenomicMarker() {}
    
    public GenomicMarker(Long userId, String rsid, String genotype) {
        this.userId = userId;
        this.rsid = rsid.toLowerCase();
        this.genotype = genotype.toUpperCase();
    }
    
    // ============== GETTERS AND SETTERS ==============
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getRsid() { return rsid; }
    public void setRsid(String rsid) { this.rsid = rsid != null ? rsid.toLowerCase() : null; }
    
    public String getGenotype() { return genotype; }
    public void setGenotype(String genotype) { this.genotype = genotype != null ? genotype.toUpperCase() : null; }
    
    public String getChromosome() { return chromosome; }
    public void setChromosome(String chromosome) { this.chromosome = chromosome; }
    
    public Long getPosition() { return position; }
    public void setPosition(Long position) { this.position = position; }
    
    public String getGene() { return gene; }
    public void setGene(String gene) { this.gene = gene; }
    
    public MarkerCategory getCategory() { return category; }
    public void setCategory(MarkerCategory category) { this.category = category; }
    
    public Significance getSignificance() { return significance; }
    public void setSignificance(Significance significance) { this.significance = significance; }
    
    public String getInterpretation() { return interpretation; }
    public void setInterpretation(String interpretation) { this.interpretation = interpretation; }
}
