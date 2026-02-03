package com.health.repository;

import com.health.entity.FoodLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {
    List<FoodLog> findByUserId(Long userId);
    List<FoodLog> findByUserIdAndDate(Long userId, String date);
    List<FoodLog> findByUserIdOrderByDateDesc(Long userId);
    List<FoodLog> findByUserIdAndDateGreaterThanEqual(Long userId, String date);
}
