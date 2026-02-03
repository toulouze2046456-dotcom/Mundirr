package com.health.repository;

import com.health.entity.SubstanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubstanceLogRepository extends JpaRepository<SubstanceLog, Long> {
    List<SubstanceLog> findByUserId(Long userId);
    Optional<SubstanceLog> findByUserIdAndDateAndType(Long userId, String date, String type);
    List<SubstanceLog> findByUserIdAndDateGreaterThanEqual(Long userId, String date);
    List<SubstanceLog> findByUserIdOrderByDateDesc(Long userId);
}
