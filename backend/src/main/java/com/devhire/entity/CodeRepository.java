package com.devhire.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "code_repositories",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "user_id",
                                "github_repository_id"
                        }
                )
        }
)
public class CodeRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "github_repository_id", nullable = false)
    private Long githubRepositoryId;

    @Column(name = "github_username", nullable = false)
    private String githubUsername;

    @Column(nullable = false)
    private String name;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "primary_language")
    private String primaryLanguage;

    private Integer stars;

    private Integer forks;

    @Column(columnDefinition = "TEXT")
    private String topics;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "github_updated_at")
    private String githubUpdatedAt;

    @Column(nullable = false)
    private Boolean forked;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    public CodeRepository() {
    }

    public CodeRepository(
            User user,
            Long githubRepositoryId,
            String githubUsername
    ) {
        this.user = user;
        this.githubRepositoryId = githubRepositoryId;
        this.githubUsername = githubUsername;
    }

    public void updateMetadata(
            String name,
            String fullName,
            String description,
            String primaryLanguage,
            Integer stars,
            Integer forks,
            String topics,
            String githubUrl,
            String githubUpdatedAt,
            Boolean forked
    ) {
        this.name = name;
        this.fullName = fullName;
        this.description = description;
        this.primaryLanguage = primaryLanguage;
        this.stars = stars;
        this.forks = forks;
        this.topics = topics;
        this.githubUrl = githubUrl;
        this.githubUpdatedAt = githubUpdatedAt;
        this.forked = forked;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Long getGithubRepositoryId() {
        return githubRepositoryId;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public Integer getStars() {
        return stars;
    }

    public Integer getForks() {
        return forks;
    }

    public String getTopics() {
        return topics;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getGithubUpdatedAt() {
        return githubUpdatedAt;
    }

    public Boolean getForked() {
        return forked;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void markAnalyzed() {
        this.analyzedAt = LocalDateTime.now();
    }
}