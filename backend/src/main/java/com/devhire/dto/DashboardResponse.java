package com.devhire.dto;

import java.util.List;

public record DashboardResponse(
        String fullName,
        Double latestMatchScore,

        int matchedCount,
        int partialCount,
        int missingCount,

        int strongGithubEvidence,
        int moderateGithubEvidence,
        int weakGithubEvidence,

        List<String> topSkillsToLearn,

        List<RecentAnalysisResponse> recentAnalyses
) {
}