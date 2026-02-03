package com.health.controller;

import com.health.entity.HealthUuidRegistry;
import com.health.entity.MundWallet;
import com.health.entity.User;
import com.health.repository.HealthUuidRegistryRepository;
import com.health.repository.MundWalletRepository;
import com.health.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Zero-Trust Health Verification Controller
 * Handles UUID checking and registration to prevent duplicate $MUND rewards
 */
@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthVerificationController {

    @Autowired
    private HealthUuidRegistryRepository uuidRepository;

    @Autowired
    private MundWalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Check if a UUID has already been processed
     */
    @PostMapping("/uuid/check")
    public ResponseEntity<Map<String, Object>> checkUuid(@RequestBody Map<String, String> request) {
        String uuid = request.get("uuid");
        String type = request.get("type");

        if (uuid == null || uuid.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "UUID required"));
        }

        boolean exists = uuidRepository.existsByHealthkitUuid(uuid);

        Map<String, Object> response = new HashMap<>();
        response.put("isNew", !exists);
        response.put("uuid", uuid);

        return ResponseEntity.ok(response);
    }

    /**
     * Register a processed UUID (called after $MUND is awarded)
     */
    @PostMapping("/uuid/register")
    public ResponseEntity<Map<String, Object>> registerUuid(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        String uuid = (String) request.get("uuid");
        String type = (String) request.get("type");
        Integer mundEarned = (Integer) request.get("mundEarned");

        if (uuid == null || type == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "UUID and type required"));
        }

        // Check if already exists
        if (uuidRepository.existsByHealthkitUuid(uuid)) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "reason", "UUID_ALREADY_REGISTERED"
            ));
        }

        // Get user ID from authentication or use 0 for anonymous
        Long userId = 0L;
        if (authentication != null && authentication.getName() != null) {
            Optional<User> user = userRepository.findByEmail(authentication.getName());
            if (user.isPresent()) {
                userId = user.get().getId();
            }
        }

        // Register the UUID
        HealthUuidRegistry registry = new HealthUuidRegistry(
                uuid,
                userId,
                type,
                mundEarned != null ? mundEarned : 0
        );

        uuidRepository.save(registry);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "uuid", uuid
        ));
    }

    /**
     * Get $MUND wallet for authenticated user
     */
    @GetMapping("/wallet")
    public ResponseEntity<Map<String, Object>> getWallet(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        MundWallet wallet = walletRepository.findByUser(user)
                .orElseGet(() -> {
                    // Create new wallet if doesn't exist
                    MundWallet newWallet = new MundWallet(user);
                    return walletRepository.save(newWallet);
                });

        Map<String, Object> response = new HashMap<>();
        response.put("mundBalance", wallet.getMundBalance());
        response.put("mundLifetime", wallet.getMundLifetime());
        response.put("tier", wallet.getTier());
        response.put("tierName", MundWallet.TIER_NAMES[wallet.getTier() - 1]);
        response.put("tierBonus", wallet.getTierBonus());
        response.put("tierProgress", wallet.getTierProgress());
        response.put("mundToNextTier", wallet.getMundToNextTier());
        response.put("globalStreak", wallet.getGlobalStreak());
        response.put("lastActiveDate", wallet.getLastActiveDate());

        return ResponseEntity.ok(response);
    }

    /**
     * Add $MUND to wallet (server-verified action)
     */
    @PostMapping("/wallet/add")
    public ResponseEntity<Map<String, Object>> addMund(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        Long amount = ((Number) request.get("amount")).longValue();
        String reason = (String) request.get("reason");

        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Valid amount required"));
        }

        Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        MundWallet wallet = walletRepository.findByUser(user)
                .orElseGet(() -> {
                    MundWallet newWallet = new MundWallet(user);
                    return walletRepository.save(newWallet);
                });

        int oldTier = wallet.getTier();
        wallet.addMund(amount);
        walletRepository.save(wallet);

        boolean tieredUp = wallet.getTier() > oldTier;

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("mundBalance", wallet.getMundBalance());
        response.put("mundLifetime", wallet.getMundLifetime());
        response.put("tier", wallet.getTier());
        response.put("tieredUp", tieredUp);
        if (tieredUp) {
            response.put("newTierName", MundWallet.TIER_NAMES[wallet.getTier() - 1]);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Spend $MUND from wallet
     */
    @PostMapping("/wallet/spend")
    public ResponseEntity<Map<String, Object>> spendMund(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        Long amount = ((Number) request.get("amount")).longValue();
        String reason = (String) request.get("reason");

        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Valid amount required"));
        }

        Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        MundWallet wallet = walletRepository.findByUser(user)
                .orElse(null);

        if (wallet == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "reason", "NO_WALLET"
            ));
        }

        boolean success = wallet.spendMund(amount);

        if (!success) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "reason", "INSUFFICIENT_BALANCE"
            ));
        }

        walletRepository.save(wallet);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "mundBalance", wallet.getMundBalance()
        ));
    }

    /**
     * Get daily limits status for current user
     */
    @GetMapping("/limits")
    public ResponseEntity<Map<String, Object>> getDailyLimits(Authentication authentication) {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);

        Long userId = 0L;
        if (authentication != null && authentication.getName() != null) {
            Optional<User> user = userRepository.findByEmail(authentication.getName());
            if (user.isPresent()) {
                userId = user.get().getId();
            }
        }

        // Count today's processed items by type
        long stepsToday = uuidRepository.countByUserIdAndDataTypeAndProcessedAtAfter(
                userId, "stepCount", startOfDay);
        long workoutsToday = uuidRepository.countByUserIdAndDataTypeAndProcessedAtAfter(
                userId, "workout", startOfDay);
        long sleepToday = uuidRepository.countByUserIdAndDataTypeAndProcessedAtAfter(
                userId, "sleep", startOfDay);

        Map<String, Object> response = new HashMap<>();
        response.put("stepsProcessed", stepsToday);
        response.put("workoutsProcessed", workoutsToday);
        response.put("sleepProcessed", sleepToday);

        // Maximum limits
        response.put("maxDailyStepMund", 100);
        response.put("maxDailyWorkouts", 2);
        response.put("maxDailyAds", 2);
        response.put("maxDailyMeals", 3);

        return ResponseEntity.ok(response);
    }
}
