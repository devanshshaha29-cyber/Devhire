package com.devhire.service;

import com.devhire.dto.AiLearningPlanResult;
import com.devhire.dto.SkillExtractionResult;

import java.util.List;

public interface AiProvider {

    SkillExtractionResult extractResumeSkills(String resumeText);

    SkillExtractionResult extractJobSkills(String jobDescription);

    AiLearningPlanResult generateLearningPlan(
            String jobTitle,
            double overallScore,
            List<String> prioritySkills
    );
}