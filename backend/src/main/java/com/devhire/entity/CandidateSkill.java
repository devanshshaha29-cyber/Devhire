package com.devhire.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "candidate_skills")
public class CandidateSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(nullable = false)
    private Double confidence;

    public CandidateSkill() {
    }

    public CandidateSkill(
            Resume resume,
            Skill skill,
            Double confidence
    ) {
        this.resume = resume;
        this.skill = skill;
        this.confidence = confidence;
    }

    public Long getId() {
        return id;
    }

    public Resume getResume() {
        return resume;
    }

    public Skill getSkill() {
        return skill;
    }

    public Double getConfidence() {
        return confidence;
    }
}