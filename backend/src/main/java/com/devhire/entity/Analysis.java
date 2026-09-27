package com.devhire.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analyses")
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(name = "base_score")
    private Double baseScore;

    @Column(name = "github_bonus")
    private Double githubBonus;

    @Column(name = "overall_score", nullable = false)
    private Double overallScore;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt =
            LocalDateTime.now();

    public Analysis() {
    }

    public Analysis(
            User user,
            Resume resume,
            Job job,
            Double baseScore,
            Double githubBonus,
            Double overallScore
    ) {
        this.user = user;
        this.resume = resume;
        this.job = job;
        this.baseScore = baseScore;
        this.githubBonus = githubBonus;
        this.overallScore = overallScore;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Resume getResume() {
        return resume;
    }

    public Job getJob() {
        return job;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}