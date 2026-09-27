package com.devhire.dto;

import java.util.List;

public record LearningPlanResponse(

        Long analysisId,

        String targetJob,

        Double matchScore,

        List<LearningRecommendationResponse>
                recommendations

) {
}