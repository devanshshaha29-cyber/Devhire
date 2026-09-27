package com.devhire.dto;

import java.time.LocalDateTime;

public record ResumeSummaryResponse(
        Long id,
        String originalFileName,
        String status,
        LocalDateTime uploadedAt
) {
}