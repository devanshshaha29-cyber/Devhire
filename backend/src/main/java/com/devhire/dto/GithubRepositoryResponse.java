package com.devhire.dto;

import java.util.List;

public record GithubRepositoryResponse(
        Long id,
        String name,
        String description,
        String primaryLanguage,
        Integer stars,
        Integer forks,
        List<String> topics,
        String githubUrl,
        String updatedAt,
        Boolean forked
) {
}