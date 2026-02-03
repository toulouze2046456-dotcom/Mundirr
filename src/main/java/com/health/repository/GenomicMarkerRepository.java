package com.health.repository;

import com.health.entity.GenomicMarker;
import com.health.entity.GenomicMarker.MarkerCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenomicMarkerRepository extends JpaRepository<GenomicMarker, Long> {
    
    List<GenomicMarker> findByUserId(Long userId);
    
    List<GenomicMarker> findByUserIdAndCategory(Long userId, MarkerCategory category);
    
    Optional<GenomicMarker> findByUserIdAndRsid(Long userId, String rsid);
    
    List<GenomicMarker> findByUserIdAndRsidIn(Long userId, List<String> rsids);
    
    void deleteByUserId(Long userId);
    
    long countByUserId(Long userId);
}
