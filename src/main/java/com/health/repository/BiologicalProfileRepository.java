package com.health.repository;

import com.health.entity.BiologicalProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BiologicalProfileRepository extends JpaRepository<BiologicalProfile, Long> {
    
    Optional<BiologicalProfile> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
    
    void deleteByUserId(Long userId);
}
