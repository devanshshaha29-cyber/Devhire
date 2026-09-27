package com.devhire.repository;

import com.devhire.entity.AnalysisSkillResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisSkillResultRepository
        extends JpaRepository<
        AnalysisSkillResult,
        Long
        > {

    List<AnalysisSkillResult>
    findByAnalysisId(Long analysisId);
}