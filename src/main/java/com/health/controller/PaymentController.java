package com.health.controller;

import com.health.entity.User;
import com.health.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

/**
 * Identity Wall — prevents multi-account abuse at the payment gate.
 *
 * POST /api/payment/checkout
 *   Body: { userId, cardholderName, billingAddress }
 *
 * 1. Normalize both strings (lowercase, strip special chars, collapse whitespace).
 * 2. SHA-256( normalizedName + normalizedAddress ).
 * 3. If hash already exists → 409 Conflict.
 * 4. Otherwise persist hash on user → 200 OK.
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> body) {
        try {
            Number userIdNum = (Number) body.get("userId");
            String cardholderName = (String) body.get("cardholderName");
            String billingAddress = (String) body.get("billingAddress");

            if (userIdNum == null || cardholderName == null || billingAddress == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "userId, cardholderName, and billingAddress are required"
                ));
            }

            Long userId = userIdNum.longValue();
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            // Normalize: lowercase → strip non-alphanumeric (keep spaces) → collapse whitespace → trim
            String normName = normalize(cardholderName);
            String normAddress = normalize(billingAddress);

            // SHA-256 identity hash
            String identityHash = sha256(normName + normAddress);

            // Check for duplicate identity
            if (userRepository.existsByIdentityHash(identityHash)) {
                logger.warn("Identity Wall: duplicate hash detected for userId={}", userId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "This payment identity is already associated with another account",
                    "code", "IDENTITY_DUPLICATE"
                ));
            }

            // Persist identity hash on user
            User user = userOpt.get();
            user.setIdentityHash(identityHash);
            userRepository.save(user);

            logger.info("Identity Wall: hash persisted for userId={}", userId);
            return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "Payment identity verified"
            ));

        } catch (Exception e) {
            logger.error("Payment checkout error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    /**
     * Normalize a string: lowercase, strip all non-alphanumeric except spaces,
     * collapse multiple whitespace into one, trim.
     */
    private String normalize(String input) {
        if (input == null) return "";
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * SHA-256 hex digest.
     */
    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
