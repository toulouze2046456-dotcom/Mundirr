package com.health.repository;

import com.health.entity.SupplementLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplementLogRepository extends JpaRepository<SupplementLog, Long> {
    List<SupplementLog> findByUserIdAndDate(Long userId, String date);
    Optional<SupplementLog> findByUserIdAndSupplementIdAndDate(Long userId, Long supplementId, String date);
}
