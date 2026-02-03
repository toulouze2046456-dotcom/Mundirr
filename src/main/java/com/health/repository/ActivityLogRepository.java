package com.health.repository;

import com.health.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUserIdAndType(Long userId, String type);
    List<ActivityLog> findByUserIdAndTypeAndDate(Long userId, String type, String date);
    List<ActivityLog> findByUserIdAndDateGreaterThanEqual(Long userId, String date);
    List<ActivityLog> findByUserIdOrderByDateDesc(Long userId);
}
