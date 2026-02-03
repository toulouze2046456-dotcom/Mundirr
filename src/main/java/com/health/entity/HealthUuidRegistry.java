package com.health.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores processed HealthKit UUIDs to prevent duplicate $MUND rewards.
 * This is the backend validation layer for the zero-trust system.
 */
@Entity
@Table(name = "health_uuid_registry", indexes = {
    @Index(name = "idx_uuid", columnList = "healthkitUuid", unique = true),
    @Index(name = "idx_user_type", columnList = "userId, dataType")
})
public class HealthUuidRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String healthkitUuid;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 32)
    private String dataType; // stepCount, workout, sleep, etc.

    @Column(nullable = false)
    private Integer mundEarned;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    @Column(length = 64)
    private String deviceName;

    @Column(length = 32)
    private String deviceModel;

    // Constructors
    public HealthUuidRegistry() {
        this.processedAt = LocalDateTime.now();
    }

    public HealthUuidRegistry(String healthkitUuid, Long userId, String dataType, Integer mundEarned) {
        this.healthkitUuid = healthkitUuid;
        this.userId = userId;
        this.dataType = dataType;
        this.mundEarned = mundEarned;
        this.processedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHealthkitUuid() {
        return healthkitUuid;
    }

    public void setHealthkitUuid(String healthkitUuid) {
        this.healthkitUuid = healthkitUuid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getMundEarned() {
        return mundEarned;
    }

    public void setMundEarned(Integer mundEarned) {
        this.mundEarned = mundEarned;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
}
