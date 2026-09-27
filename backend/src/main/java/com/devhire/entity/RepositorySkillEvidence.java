package com.devhire.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "repository_skill_evidence",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "repository_id",
                                "skill_id"
                        }
                )
        }
)
public class RepositorySkillEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id")
    private CodeRepository repository;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "evidence_level", nullable = false)
    private String evidenceLevel;

    @Column(nullable = false)
    private Double confidence;

    @Column(
            name = "evidence_summary",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String evidenceSummary;

    public RepositorySkillEvidence() {
    }

    public RepositorySkillEvidence(
            CodeRepository repository,
            Skill skill,
            String evidenceLevel,
            Double confidence,
            String evidenceSummary
    ) {
        this.repository = repository;
        this.skill = skill;
        this.evidenceLevel = evidenceLevel;
        this.confidence = confidence;
        this.evidenceSummary = evidenceSummary;
    }

    public Long getId() {
        return id;
    }

    public CodeRepository getRepository() {
        return repository;
    }

    public Skill getSkill() {
        return skill;
    }

    public String getEvidenceLevel() {
        return evidenceLevel;
    }

    public Double getConfidence() {
        return confidence;
    }

    public String getEvidenceSummary() {
        return evidenceSummary;
    }
}