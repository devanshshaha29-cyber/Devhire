package com.devhire.dto;

import java.util.ArrayList;
import java.util.List;

public class AiLearningPlanResult {

    private List<AiLearningRecommendation> recommendations =
            new ArrayList<>();

    public AiLearningPlanResult() {
    }

    public List<AiLearningRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(
            List<AiLearningRecommendation> recommendations
    ) {
        this.recommendations = recommendations;
    }
}