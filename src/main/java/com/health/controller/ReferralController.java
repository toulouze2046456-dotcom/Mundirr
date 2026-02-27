package com.health.controller;

import com.health.entity.MundWallet;
import com.health.entity.PendingReferralReward;
import com.health.entity.User;
import com.health.repository.MundWalletRepository;
import com.health.repository.PendingReferralRewardRepository;
import com.health.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for Referral Rewards — §1.12
 *
 * Endpoints:
 *   GET  /api/referral/code          → Get or generate referral code
 *   GET  /api/referral/history       → Referral history (pending / verified / purged)
 *   POST /api/referral/verify        → Webhook: subscription confirmed → unlock reward
 *   POST /api/referral/purge         → Anti-inflation: purge pending reward
 */
@RestController
@RequestMapping("/api/referral")
public class ReferralController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ReferralController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PendingReferralRewardRepository rewardRepository;

    @Autowired
    private MundWalletRepository walletRepository;

    // ─── GET /api/referral/code ────────────────────────────────────────────────

    @GetMapping("/code")
    public ResponseEntity<?> getReferralCode(HttpServletRequest request) {
        try {
            Long userId = getAuthenticatedUserId(request);

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            String code = user.getReferralCode();
            if (code == null || code.isBlank()) {
                // Shouldn't happen (generated at registration), but generate fallback
                code = user.getName() != null
                        ? user.getName().toLowerCase().replaceAll("[^a-z0-9]", "").substring(0, Math.min(12, user.getName().length()))
                        : "user";
                code += "-" + Long.toHexString(System.currentTimeMillis()).substring(6);
                user.setReferralCode(code);
                userRepository.save(user);
            }

            return ResponseEntity.ok(Map.of(
                    "code", code,
                    "deepLink", "https://mundir.app/invite/" + code
            ));
        } catch (Exception e) {
            logger.error("Error getting referral code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get referral code"));
        }
    }

    // ─── GET /api/referral/history ─────────────────────────────────────────────

    @GetMapping("/history")
    public ResponseEntity<?> getReferralHistory(HttpServletRequest request) {
        try {
            Long userId = getAuthenticatedUserId(request);

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            List<PendingReferralReward> rewards = rewardRepository.findByReferrer(user);
            int pendingMund = rewardRepository.sumPendingAmountByReferrer(user);

            List<Map<String, Object>> history = new ArrayList<>();
            for (PendingReferralReward r : rewards) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", r.getId());
                entry.put("referredName", r.getReferredName());
                entry.put("referredEmail", r.getReferredEmail());
                entry.put("amount", r.getAmount());
                entry.put("status", r.getStatus());
                entry.put("createdAt", r.getCreatedAt().toString());
                entry.put("verifiedAt", r.getVerifiedAt() != null ? r.getVerifiedAt().toString() : null);
                entry.put("purgedAt", r.getPurgedAt() != null ? r.getPurgedAt().toString() : null);
                entry.put("purgeReason", r.getPurgeReason());
                history.add(entry);
            }

            return ResponseEntity.ok(Map.of(
                    "history", history,
                    "pendingMund", pendingMund,
                    "totalInvited", rewards.size(),
                    "pendingCount", rewards.stream().filter(r -> "pending".equals(r.getStatus())).count(),
                    "verifiedCount", rewards.stream().filter(r -> "verified".equals(r.getStatus())).count()
            ));
        } catch (Exception e) {
            logger.error("Error getting referral history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get referral history"));
        }
    }

    // ─── POST /api/referral/verify ─────────────────────────────────────────────
    // Called by subscription_confirmed webhook

    @PostMapping("/verify")
    public ResponseEntity<?> verifyReferral(@RequestBody Map<String, Object> body) {
        try {
            // The referred user's ID (from webhook payload)
            Long referredUserId = body.get("referredUserId") != null
                    ? Long.parseLong(body.get("referredUserId").toString())
                    : null;
            if (referredUserId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "referredUserId required"));
            }

            User referred = userRepository.findById(referredUserId).orElse(null);
            if (referred == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Referred user not found"));
            }

            List<PendingReferralReward> pending = rewardRepository.findByReferredAndStatus(referred, "pending");
            if (pending.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No pending referral to verify"));
            }

            int totalUnlocked = 0;
            for (PendingReferralReward reward : pending) {
                reward.setStatus("verified");
                reward.setVerifiedAt(LocalDateTime.now());
                rewardRepository.save(reward);

                // Credit 500 $MUND to referrer's real wallet
                User referrer = reward.getReferrer();
                MundWallet wallet = walletRepository.findByUser(referrer)
                        .orElseGet(() -> {
                            MundWallet w = new MundWallet(referrer);
                            return walletRepository.save(w);
                        });
                wallet.addMund(reward.getAmount());
                walletRepository.save(wallet);
                totalUnlocked += reward.getAmount();

                logger.info("Referral verified: {} → {} (+{} $MUND unlocked)",
                        referrer.getEmail(), referred.getEmail(), reward.getAmount());
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "totalUnlocked", totalUnlocked,
                    "rewardsVerified", pending.size()
            ));
        } catch (Exception e) {
            logger.error("Error verifying referral", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Verification failed"));
        }
    }

    // ─── POST /api/referral/purge ──────────────────────────────────────────────
    // Anti-inflation: purge a pending reward (account deleted / bot flagged)

    @PostMapping("/purge")
    public ResponseEntity<?> purgeReferral(@RequestBody Map<String, Object> body) {
        try {
            Long rewardId = body.get("rewardId") != null
                    ? Long.parseLong(body.get("rewardId").toString())
                    : null;
            String reason = (String) body.getOrDefault("reason", "admin");

            if (rewardId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "rewardId required"));
            }

            PendingReferralReward reward = rewardRepository.findById(rewardId).orElse(null);
            if (reward == null || !"pending".equals(reward.getStatus())) {
                return ResponseEntity.ok(Map.of("message", "Reward not found or already processed"));
            }

            reward.setStatus("purged");
            reward.setPurgedAt(LocalDateTime.now());
            reward.setPurgeReason(reason);
            rewardRepository.save(reward);

            logger.info("Referral reward purged: id={} reason={}", rewardId, reason);

            return ResponseEntity.ok(Map.of("success", true, "purgedAmount", reward.getAmount()));
        } catch (Exception e) {
            logger.error("Error purging referral", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Purge failed"));
        }
    }
}
