package com.devhire.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "learning_recommendations")
public class LearningRecommendation {

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
    private Integer priority;

    @Column(name = "priority_level", nullable = false)
    private String priorityLevel;

    @Column(name = "why_needed", columnDefinition = "TEXT")
    private String whyNeeded;

    @Column(name = "what_to_learn", columnDefinition = "TEXT")
    private String whatToLearn;

    @Column(name = "learning_sequence", columnDefinition = "TEXT")
    private String learningSequence;

    @Column(name = "suggested_project", columnDefinition = "TEXT")
    private String suggestedProject;

    @Column
    private String difficulty;

    public LearningRecommendation() {
    }

    public LearningRecommendation(
            Analysis analysis,
            Skill skill,
            Integer priority,
            String priorityLevel,
            String whyNeeded,
            String whatToLearn,
            String learningSequence,
            String suggestedProject,
            String difficulty
    ) {
        this.analysis = analysis;
        this.skill = skill;
        this.priority = priority;
        this.priorityLevel = priorityLevel;
        this.whyNeeded = whyNeeded;
        this.whatToLearn = whatToLearn;
        this.learningSequence = learningSequence;
        this.suggestedProject = suggestedProject;
        this.difficulty = difficulty;
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

    public Integer getPriority() {
        return priority;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public String getWhyNeeded() {
        return whyNeeded;
    }

    public String getWhatToLearn() {
        return whatToLearn;
    }

    public String getLearningSequence() {
        return learningSequence;
    }

    public String getSuggestedProject() {
        return suggestedProject;
    }

    public String getDifficulty() {
        return difficulty;
    }
}