package com.health.repository;

import com.health.entity.FinanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceTransactionRepository extends JpaRepository<FinanceTransaction, Long> {
    List<FinanceTransaction> findByUserId(Long userId);
    List<FinanceTransaction> findByUserIdAndType(Long userId, String type);
    List<FinanceTransaction> findByUserIdAndCategory(Long userId, String category);
    List<FinanceTransaction> findByUserIdAndDateGreaterThanEqual(Long userId, String date);
    List<FinanceTransaction> findByUserIdOrderByDateDesc(Long userId);
}
