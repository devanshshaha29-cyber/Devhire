package com.devhire.dto;

import java.util.List;

public record GithubAnalysisResponse(
        String username,
        int repositoryCount,
        int analyzedRepositoryCount,
        List<GithubRepositoryEvidenceResponse> repositories
) {
}