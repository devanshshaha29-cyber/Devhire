package com.devhire.repository;

import com.devhire.entity.LearningRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningRecommendationRepository
        extends JpaRepository<LearningRecommendation, Long> {

    List<LearningRecommendation>
    findByAnalysisIdOrderByPriorityAsc(Long analysisId);

    void deleteByAnalysisId(Long analysisId);
}