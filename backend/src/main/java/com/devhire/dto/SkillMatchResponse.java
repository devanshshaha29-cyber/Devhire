package com.devhire.dto;

public class SkillMatchResponse {

    private String skill;
    private String requirementLevel;
    private String status;

    private Double candidateConfidence;

    private String githubEvidenceLevel;
    private Double githubConfidence;

    public SkillMatchResponse(
            String skill,
            String requirementLevel,
            String status,
            Double candidateConfidence,
            String githubEvidenceLevel,
            Double githubConfidence
    ) {
        this.skill = skill;
        this.requirementLevel = requirementLevel;
        this.status = status;
        this.candidateConfidence = candidateConfidence;
        this.githubEvidenceLevel = githubEvidenceLevel;
        this.githubConfidence = githubConfidence;
    }

    public String getSkill() {
        return skill;
    }

    public String getRequirementLevel() {
        return requirementLevel;
    }

    public String getStatus() {
        return status;
    }

    public Double getCandidateConfidence() {
        return candidateConfidence;
    }

    public String getGithubEvidenceLevel() {
        return githubEvidenceLevel;
    }

    public Double getGithubConfidence() {
        return githubConfidence;
    }
}