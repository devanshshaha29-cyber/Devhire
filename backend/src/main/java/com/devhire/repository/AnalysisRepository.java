package com.devhire.repository;

import com.devhire.entity.Analysis;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository
        extends JpaRepository<Analysis, Long> {

    List<Analysis>
    findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Analysis>
    findByIdAndUserId(
            Long analysisId,
            Long userId
    );
}