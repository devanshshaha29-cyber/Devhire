package com.devhire.dto;

public record LearningRecommendationResponse(

        Integer priority,

        String priorityLevel,

        String skill,

        String status,

        String requirementLevel,

        String whyNeeded,

        String whatToLearn,

        String learningSequence,

        String suggestedProject,

        String difficulty

) {
}