package com.health.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * User's $MUND wallet - stores balance and lifetime earnings.
 * This is the single source of truth for $MUND economy.
 */
@Entity
@Table(name = "mund_wallet")
public class MundWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Long mundBalance = 0L; // Spendable $MUND

    @Column(nullable = false)
    private Long mundLifetime = 0L; // Total $MUND ever earned (never decreases)

    @Column(nullable = false)
    private Integer tier = 1; // Current tier (1-5)

    @Column(nullable = false)
    private Integer globalStreak = 0; // Consecutive days active

    @Column
    private LocalDate lastActiveDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Tier thresholds
    public static final long[] TIER_THRESHOLDS = {0, 1000, 5000, 15000, 50000};
    public static final String[] TIER_NAMES = {"INITIATE", "OPERATOR", "SPECIALIST", "COMMANDER", "SENTINEL"};
    public static final double[] TIER_BONUSES = {1.00, 1.01, 1.02, 1.03, 1.05};

    // Constructors
    public MundWallet() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public MundWallet(User user) {
        this.user = user;
        this.mundBalance = 0L;
        this.mundLifetime = 0L;
        this.tier = 1;
        this.globalStreak = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Add $MUND to wallet - updates both balance and lifetime
     */
    public void addMund(long amount) {
        this.mundBalance += amount;
        this.mundLifetime += amount;
        updateTier();
        updateStreak();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Spend $MUND from balance (does NOT affect lifetime)
     */
    public boolean spendMund(long amount) {
        if (amount > this.mundBalance) {
            return false;
        }
        this.mundBalance -= amount;
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    /**
     * Update tier based on lifetime $MUND
     */
    private void updateTier() {
        for (int i = TIER_THRESHOLDS.length - 1; i >= 0; i--) {
            if (this.mundLifetime >= TIER_THRESHOLDS[i]) {
                this.tier = i + 1;
                break;
            }
        }
    }

    /**
     * Update streak based on last active date
     */
    private void updateStreak() {
        LocalDate today = LocalDate.now();
        
        if (this.lastActiveDate == null) {
            this.globalStreak = 1;
        } else if (this.lastActiveDate.equals(today.minusDays(1))) {
            this.globalStreak++;
        } else if (!this.lastActiveDate.equals(today)) {
            this.globalStreak = 1; // Streak broken
        }
        
        this.lastActiveDate = today;
    }

    /**
     * Get current tier efficiency bonus
     */
    public double getTierBonus() {
        if (tier >= 1 && tier <= TIER_BONUSES.length) {
            return TIER_BONUSES[tier - 1];
        }
        return 1.0;
    }

    /**
     * Get $MUND needed for next tier
     */
    public Long getMundToNextTier() {
        if (tier >= TIER_THRESHOLDS.length) {
            return null; // Max tier
        }
        return TIER_THRESHOLDS[tier] - mundLifetime;
    }

    /**
     * Get tier progress percentage
     */
    public int getTierProgress() {
        if (tier >= TIER_THRESHOLDS.length) {
            return 100;
        }
        long currentMin = TIER_THRESHOLDS[tier - 1];
        long nextMin = TIER_THRESHOLDS[tier];
        long progress = mundLifetime - currentMin;
        long range = nextMin - currentMin;
        return (int) Math.min(100, (progress * 100) / range);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getMundBalance() {
        return mundBalance;
    }

    public void setMundBalance(Long mundBalance) {
        this.mundBalance = mundBalance;
    }

    public Long getMundLifetime() {
        return mundLifetime;
    }

    public void setMundLifetime(Long mundLifetime) {
        this.mundLifetime = mundLifetime;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    public Integer getGlobalStreak() {
        return globalStreak;
    }

    public void setGlobalStreak(Integer globalStreak) {
        this.globalStreak = globalStreak;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
