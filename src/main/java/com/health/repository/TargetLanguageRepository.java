package com.health.repository;

import com.health.entity.TargetLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetLanguageRepository extends JpaRepository<TargetLanguage, Long> {
    List<TargetLanguage> findByUserId(Long userId);
}
