package com.health.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Pending Referral Reward — §1.12 Anti-Fraud Locking.
 *
 * 500 $MUND are credited to PendingBalance on signup but CANNOT be spent
 * or used for Bulwark stakes until a subscription_confirmed webhook fires
 * for the referred UID.
 *
 * If the referred account is deleted or flagged as a bot, the reward is purged.
 */
@Entity
@Table(name = "pending_referral_rewards")
public class PendingReferralReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who shared the referral code (the inviter). */
    @ManyToOne
    @JoinColumn(name = "referrer_id", nullable = false)
    private User referrer;

    /** The user who signed up via the referral link (null until signup). */
    @ManyToOne
    @JoinColumn(name = "referred_id")
    private User referred;

    /** Email the invite was sent to (for matching before signup). */
    @Column(nullable = false)
    private String referredEmail;

    /** Display name of the referred friend. */
    private String referredName;

    /** $MUND amount locked (always 500). */
    @Column(nullable = false)
    private Integer amount = 500;

    /** pending | verified | purged */
    @Column(nullable = false)
    private String status = "pending";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** When subscription_confirmed webhook was received. */
    private LocalDateTime verifiedAt;

    /** When the reward was purged (bot/deleted). */
    private LocalDateTime purgedAt;

    /** account_deleted | bot_flagged | admin */
    private String purgeReason;

    // ─── Constructors ──────────────────────────────────────────────────────────

    public PendingReferralReward() {
        this.createdAt = LocalDateTime.now();
    }

    public PendingReferralReward(User referrer, String referredEmail, String referredName) {
        this.referrer = referrer;
        this.referredEmail = referredEmail;
        this.referredName = referredName;
        this.amount = 500;
        this.status = "pending";
        this.createdAt = LocalDateTime.now();
    }

    // ─── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getReferrer() { return referrer; }
    public void setReferrer(User referrer) { this.referrer = referrer; }

    public User getReferred() { return referred; }
    public void setReferred(User referred) { this.referred = referred; }

    public String getReferredEmail() { return referredEmail; }
    public void setReferredEmail(String referredEmail) { this.referredEmail = referredEmail; }

    public String getReferredName() { return referredName; }
    public void setReferredName(String referredName) { this.referredName = referredName; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public LocalDateTime getPurgedAt() { return purgedAt; }
    public void setPurgedAt(LocalDateTime purgedAt) { this.purgedAt = purgedAt; }

    public String getPurgeReason() { return purgeReason; }
    public void setPurgeReason(String purgeReason) { this.purgeReason = purgeReason; }
}
