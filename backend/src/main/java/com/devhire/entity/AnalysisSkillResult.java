package com.devhire.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "analysis_skill_results")
public class AnalysisSkillResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id")
    private Analysis analysis;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(nullable = false)
    private String status;

    @Column(name = "requirement_level", nullable = false)
    private String requirementLevel;

    @Column(name = "candidate_confidence")
    private Double candidateConfidence;

    @Column(name = "github_evidence_level")
    private String githubEvidenceLevel;

    @Column(name = "github_confidence")
    private Double githubConfidence;

    public AnalysisSkillResult() {
    }

    public AnalysisSkillResult(
            Analysis analysis,
            Skill skill,
            String status,
            String requirementLevel,
            Double candidateConfidence,
            String githubEvidenceLevel,
            Double githubConfidence
    ) {
        this.analysis = analysis;
        this.skill = skill;
        this.status = status;
        this.requirementLevel = requirementLevel;
        this.candidateConfidence = candidateConfidence;
        this.githubEvidenceLevel = githubEvidenceLevel;
        this.githubConfidence = githubConfidence;
    }

    public Long getId() {
        return id;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public Skill getSkill() {
        return skill;
    }

    public String getStatus() {
        return status;
    }

    public String getRequirementLevel() {
        return requirementLevel;
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