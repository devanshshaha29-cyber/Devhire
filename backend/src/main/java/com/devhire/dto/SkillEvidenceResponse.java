package com.devhire.dto;

public record SkillEvidenceResponse(
        String skill,
        String evidenceLevel,
        Double confidence,
        String evidenceSummary
) {
}