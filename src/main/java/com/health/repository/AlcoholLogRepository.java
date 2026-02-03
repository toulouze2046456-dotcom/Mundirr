package com.health.repository;

import com.health.entity.AlcoholLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlcoholLogRepository extends JpaRepository<AlcoholLog, Long> {
    List<AlcoholLog> findByUserId(Long userId);
    Optional<AlcoholLog> findByUserIdAndDate(Long userId, String date);
    List<AlcoholLog> findByUserIdAndDateGreaterThanEqual(Long userId, String date);
    List<AlcoholLog> findByUserIdOrderByDateDesc(Long userId);
}
