package com.devhire.dto;

import java.util.List;

public record GithubRepositoryEvidenceResponse(
        Long repositoryId,
        String repositoryName,
        String primaryLanguage,
        String githubUrl,
        List<SkillEvidenceResponse> evidence
) {
}