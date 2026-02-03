package com.health.repository;

import com.health.entity.HealthUuidRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthUuidRegistryRepository extends JpaRepository<HealthUuidRegistry, Long> {

    /**
     * Check if a UUID has already been processed
     */
    boolean existsByHealthkitUuid(String healthkitUuid);

    /**
     * Find by UUID
     */
    Optional<HealthUuidRegistry> findByHealthkitUuid(String healthkitUuid);

    /**
     * Find all UUIDs for a user and data type
     */
    List<HealthUuidRegistry> findByUserIdAndDataType(Long userId, String dataType);

    /**
     * Get total $MUND earned by user today
     */
    List<HealthUuidRegistry> findByUserIdAndProcessedAtAfter(Long userId, LocalDateTime after);

    /**
     * Count UUIDs processed today for a user and type
     */
    long countByUserIdAndDataTypeAndProcessedAtAfter(Long userId, String dataType, LocalDateTime after);
}
