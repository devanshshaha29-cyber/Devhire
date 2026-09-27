package com.devhire.dto;

import java.util.List;

public record GitHubApiRepository(
        Long id,
        String name,
        String full_name,
        String html_url,
        String description,
        String language,
        Integer stargazers_count,
        Integer forks_count,
        List<String> topics,
        String updated_at,
        Boolean fork
) {
}