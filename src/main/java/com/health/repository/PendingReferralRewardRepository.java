package com.health.repository;

import com.health.entity.PendingReferralReward;
import com.health.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PendingReferralRewardRepository extends JpaRepository<PendingReferralReward, Long> {

    /** All rewards created by a given referrer (any status). */
    List<PendingReferralReward> findByReferrer(User referrer);

    /** All rewards for a given referrer with a specific status. */
    List<PendingReferralReward> findByReferrerAndStatus(User referrer, String status);

    /** Prevent duplicate: check if a pending reward already exists for this email. */
    Optional<PendingReferralReward> findByReferredEmailAndStatusNot(String email, String excludedStatus);

    /** Lookup by referred user (for subscription webhook). */
    List<PendingReferralReward> findByReferredAndStatus(User referred, String status);

    /** Sum of all pending (locked) $MUND for a referrer. */
    default int sumPendingAmountByReferrer(User referrer) {
        return findByReferrerAndStatus(referrer, "pending")
                .stream()
                .mapToInt(PendingReferralReward::getAmount)
                .sum();
    }
}
