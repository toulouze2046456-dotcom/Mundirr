package com.health.repository;

import com.health.entity.MundWallet;
import com.health.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MundWalletRepository extends JpaRepository<MundWallet, Long> {

    /**
     * Find wallet by user
     */
    Optional<MundWallet> findByUser(User user);

    /**
     * Find wallet by user ID
     */
    Optional<MundWallet> findByUserId(Long userId);

    /**
     * Check if user has a wallet
     */
    boolean existsByUserId(Long userId);
}
