package com.devhire.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "job_skills")
public class JobSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(
            name = "requirement_level",
            nullable = false
    )
    private String requirementLevel;

    @Column(nullable = false)
    private Double confidence;

    public JobSkill() {
    }

    public JobSkill(
            Job job,
            Skill skill,
            String requirementLevel,
            Double confidence
    ) {
        this.job = job;
        this.skill = skill;
        this.requirementLevel = requirementLevel;
        this.confidence = confidence;
    }

    public Long getId() {
        return id;
    }

    public Job getJob() {
        return job;
    }

    public Skill getSkill() {
        return skill;
    }

    public String getRequirementLevel() {
        return requirementLevel;
    }

    public Double getConfidence() {
        return confidence;
    }
}