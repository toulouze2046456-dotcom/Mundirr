package com.health.repository;

import com.health.entity.CulturalActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CulturalActivityRepository extends JpaRepository<CulturalActivity, Long> {
    List<CulturalActivity> findByUserId(Long userId);
    List<CulturalActivity> findByUserIdAndType(Long userId, String type);
    List<CulturalActivity> findByUserIdOrderByDateDesc(Long userId);
}
