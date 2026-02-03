package com.health.repository;

import com.health.entity.PasswordResetToken;
import com.health.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
    
    void deleteByUser(User user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < ?1")
    void deleteExpiredTokens(LocalDateTime now);
    
    @Query("SELECT t FROM PasswordResetToken t WHERE t.user.id = ?1 AND t.used = false AND t.expiryDate > ?2")
    Optional<PasswordResetToken> findValidTokenByUserId(Long userId, LocalDateTime now);
}
