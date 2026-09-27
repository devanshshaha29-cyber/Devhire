package com.devhire.dto;

import java.time.LocalDateTime;

public record RecentAnalysisResponse(
        Long analysisId,
        String jobTitle,
        Double score,
        LocalDateTime createdAt
) {
}