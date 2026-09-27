package com.devhire.dto;

import java.util.List;

public class AnalysisResponse {

    private Long analysisId;

    private Double baseScore;
    private Double githubBonus;
    private Double overallScore;

    private int matchedCount;
    private int partialCount;
    private int missingCount;

    private List<SkillMatchResponse> skills;

    public AnalysisResponse(
            Long analysisId,
            Double baseScore,
            Double githubBonus,
            Double overallScore,
            int matchedCount,
            int partialCount,
            int missingCount,
            List<SkillMatchResponse> skills
    ) {
        this.analysisId = analysisId;
        this.baseScore = baseScore;
        this.githubBonus = githubBonus;
        this.overallScore = overallScore;
        this.matchedCount = matchedCount;
        this.partialCount = partialCount;
        this.missingCount = missingCount;
        this.skills = skills;
    }

    public Long getAnalysisId() {
        return analysisId;
    }

    public Double getBaseScore() {
        return baseScore;
    }

    public Double getGithubBonus() {
        return githubBonus;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public int getMatchedCount() {
        return matchedCount;
    }

    public int getPartialCount() {
        return partialCount;
    }

    public int getMissingCount() {
        return missingCount;
    }

    public List<SkillMatchResponse> getSkills() {
        return skills;
    }
}