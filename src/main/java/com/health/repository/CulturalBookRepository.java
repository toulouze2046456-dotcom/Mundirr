package com.health.repository;

import com.health.entity.CulturalBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CulturalBookRepository extends JpaRepository<CulturalBook, Long> {
    List<CulturalBook> findByUserId(Long userId);
    List<CulturalBook> findByUserIdAndStatus(Long userId, String status);
}
