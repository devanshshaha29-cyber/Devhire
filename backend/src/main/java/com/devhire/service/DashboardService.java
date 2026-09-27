package com.devhire.service;

import com.devhire.dto.DashboardResponse;
import com.devhire.dto.RecentAnalysisResponse;
import com.devhire.entity.*;
import com.devhire.repository.*;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisSkillResultRepository resultRepository;
    private final RepositorySkillEvidenceRepository evidenceRepository;
    private final LearningRecommendationRepository learningRepository;

    public DashboardService(
            UserRepository userRepository,
            AnalysisRepository analysisRepository,
            AnalysisSkillResultRepository resultRepository,
            RepositorySkillEvidenceRepository evidenceRepository,
            LearningRecommendationRepository learningRepository
    ) {
        this.userRepository = userRepository;
        this.analysisRepository = analysisRepository;
        this.resultRepository = resultRepository;
        this.evidenceRepository = evidenceRepository;
        this.learningRepository = learningRepository;
    }

    public DashboardResponse getDashboard(
            String userEmail
    ) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        List<Analysis> analyses =
                analysisRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                user.getId()
                        );

        Double latestScore = null;

        int matched = 0;
        int partial = 0;
        int missing = 0;

        List<String> topSkills =
                new ArrayList<>();

        if (!analyses.isEmpty()) {

            Analysis latest =
                    analyses.get(0);

            latestScore =
                    latest.getOverallScore();

            List<AnalysisSkillResult> results =
                    resultRepository
                            .findByAnalysisId(
                                    latest.getId()
                            );

            for (AnalysisSkillResult result :
                    results) {

                switch (result.getStatus()) {

                    case "MATCHED" ->
                            matched++;

                    case "PARTIAL" ->
                            partial++;

                    default ->
                            missing++;
                }
            }

            List<LearningRecommendation>
                    recommendations =
                    learningRepository
                            .findByAnalysisIdOrderByPriorityAsc(
                                    latest.getId()
                            );

            topSkills =
                    recommendations
                            .stream()
                            .limit(5)
                            .map(recommendation ->
                                    recommendation
                                            .getSkill()
                                            .getName()
                            )
                            .toList();

            /*
             * If the learning plan has not been
             * generated yet, still show skill gaps.
             */
            if (topSkills.isEmpty()) {

                topSkills =
                        results
                                .stream()
                                .filter(result ->
                                        !"MATCHED".equals(
                                                result.getStatus()
                                        )
                                )
                                .sorted(
                                        Comparator.comparing(
                                                result ->
                                                        "MISSING".equals(
                                                                result.getStatus()
                                                        )
                                                                ? 0
                                                                : 1
                                        )
                                )
                                .limit(5)
                                .map(result ->
                                        result
                                                .getSkill()
                                                .getName()
                                )
                                .toList();
            }
        }

        /*
         * Keep only the strongest GitHub
         * evidence for each skill.
         */

        List<RepositorySkillEvidence> evidence =
                evidenceRepository
                        .findByRepository_User_Id(
                                user.getId()
                        );

        Map<Long, RepositorySkillEvidence>
                strongestEvidence =
                new HashMap<>();

        for (RepositorySkillEvidence item :
                evidence) {

            Long skillId =
                    item.getSkill().getId();

            RepositorySkillEvidence existing =
                    strongestEvidence
                            .get(skillId);

            if (
                    existing == null ||
                    item.getConfidence()
                            > existing.getConfidence()
            ) {

                strongestEvidence.put(
                        skillId,
                        item
                );
            }
        }

        int strong = 0;
        int moderate = 0;
        int weak = 0;

        for (
                RepositorySkillEvidence item :
                strongestEvidence.values()
        ) {

            switch (
                    item.getEvidenceLevel()
            ) {

                case "STRONG" ->
                        strong++;

                case "MODERATE" ->
                        moderate++;

                default ->
                        weak++;
            }
        }

        List<RecentAnalysisResponse>
                recentAnalyses =
                analyses
                        .stream()
                        .limit(5)
                        .map(analysis ->
                                new RecentAnalysisResponse(
                                        analysis.getId(),
                                        analysis
                                                .getJob()
                                                .getTitle(),
                                        analysis
                                                .getOverallScore(),
                                        analysis
                                                .getCreatedAt()
                                )
                        )
                        .toList();

        return new DashboardResponse(
                user.getFullName(),
                latestScore,

                matched,
                partial,
                missing,

                strong,
                moderate,
                weak,

                topSkills,

                recentAnalyses
        );
    }
}