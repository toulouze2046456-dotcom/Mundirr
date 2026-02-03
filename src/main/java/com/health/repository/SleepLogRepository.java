package com.health.repository;

import com.health.entity.SleepLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SleepLogRepository extends JpaRepository<SleepLog, Long> {
    List<SleepLog> findByUserIdOrderByDateDesc(Long userId);
    Optional<SleepLog> findFirstByUserIdOrderByDateDesc(Long userId);
    Optional<SleepLog> findByUserIdAndDate(Long userId, String date);
    List<SleepLog> findByUserIdAndDateGreaterThanEqual(Long userId, String date);
}
