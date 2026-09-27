package com.devhire.service;

import com.devhire.dto.*;
import com.devhire.entity.*;
import com.devhire.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LearningPlanService {

    private final UserRepository userRepository;

    private final AnalysisRepository analysisRepository;

    private final AnalysisSkillResultRepository
            resultRepository;

    private final LearningRecommendationRepository
            learningRepository;

    private final AiProvider aiProvider;

    private final SkillNormalizationService
            normalizationService;

    public LearningPlanService(
            UserRepository userRepository,
            AnalysisRepository analysisRepository,
            AnalysisSkillResultRepository resultRepository,
            LearningRecommendationRepository learningRepository,
            AiProvider aiProvider,
            SkillNormalizationService normalizationService
    ) {

        this.userRepository = userRepository;

        this.analysisRepository =
                analysisRepository;

        this.resultRepository =
                resultRepository;

        this.learningRepository =
                learningRepository;

        this.aiProvider =
                aiProvider;

        this.normalizationService =
                normalizationService;
    }

    @Transactional
    public LearningPlanResponse generate(
            Long analysisId,
            String userEmail
    ) {

        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        Analysis analysis =
                analysisRepository
                        .findByIdAndUserId(
                                analysisId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Analysis not found"
                                )
                        );

        List<AnalysisSkillResult> allResults =
                resultRepository
                        .findByAnalysisId(
                                analysis.getId()
                        );

        List<AnalysisSkillResult> gaps =
                allResults
                        .stream()
                        .filter(result ->
                                !"MATCHED".equals(
                                        result.getStatus()
                                )
                        )
                        .sorted(
                                Comparator.comparingInt(
                                        this::priorityRank
                                )
                        )
                        .toList();

        /*
         * Candidate already matches everything.
         */
        if (gaps.isEmpty()) {

            learningRepository
                    .deleteByAnalysisId(
                            analysisId
                    );

            return new LearningPlanResponse(
                    analysisId,
                    analysis
                            .getJob()
                            .getTitle(),
                    analysis.getOverallScore(),
                    List.of()
            );
        }

        List<String> aiInput =
                gaps
                        .stream()
                        .map(result ->
                                result
                                        .getSkill()
                                        .getName()
                                        +
                                " | "
                                        +
                                result
                                        .getRequirementLevel()
                                        +
                                " | "
                                        +
                                result.getStatus()
                        )
                        .toList();

        AiLearningPlanResult aiResult =
                aiProvider
                        .generateLearningPlan(
                                analysis
                                        .getJob()
                                        .getTitle(),

                                analysis
                                        .getOverallScore(),

                                aiInput
                        );

        Map<String, AnalysisSkillResult>
                gapMap = new HashMap<>();

        for (AnalysisSkillResult gap : gaps) {

            gapMap.put(
                    gap
                            .getSkill()
                            .getNormalizedName(),
                    gap
            );
        }

        learningRepository
                .deleteByAnalysisId(
                        analysisId
                );

        learningRepository.flush();

        List<LearningRecommendationResponse>
                responses =
                new ArrayList<>();

        Set<Long> alreadyUsedSkills =
                new HashSet<>();

        int priority = 1;

        for (
                AiLearningRecommendation aiRecommendation :
                aiResult.getRecommendations()
        ) {

            String normalized =
                    normalizationService
                            .normalize(
                                    aiRecommendation
                                            .getSkill()
                            );

            AnalysisSkillResult gap =
                    gapMap.get(normalized);

            /*
             * AI is not allowed to add skills
             * that were not actually part of
             * the analysis.
             */
            if (gap == null) {
                continue;
            }

            if (
                    alreadyUsedSkills.contains(
                            gap.getSkill().getId()
                    )
            ) {
                continue;
            }

            alreadyUsedSkills.add(
                    gap.getSkill().getId()
            );

            String priorityLevel =
                    determinePriorityLevel(
                            gap
                    );

            LearningRecommendation saved =
                    learningRepository.save(
                            new LearningRecommendation(
                                    analysis,
                                    gap.getSkill(),
                                    priority,
                                    priorityLevel,
                                    aiRecommendation
                                            .getWhyNeeded(),
                                    aiRecommendation
                                            .getWhatToLearn(),
                                    aiRecommendation
                                            .getLearningSequence(),
                                    aiRecommendation
                                            .getSuggestedProject(),
                                    normalizeDifficulty(
                                            aiRecommendation
                                                    .getDifficulty()
                                    )
                            )
                    );

            responses.add(
                    toResponse(
                            saved,
                            gap
                    )
            );

            priority++;
        }

        return new LearningPlanResponse(
                analysis.getId(),
                analysis.getJob().getTitle(),
                analysis.getOverallScore(),
                responses
        );
    }

    public LearningPlanResponse getExisting(
            Long analysisId,
            String userEmail
    ) {

        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        Analysis analysis =
                analysisRepository
                        .findByIdAndUserId(
                                analysisId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Analysis not found"
                                )
                        );

        List<AnalysisSkillResult> analysisResults =
                resultRepository
                        .findByAnalysisId(
                                analysisId
                        );

        Map<Long, AnalysisSkillResult> resultMap =
                new HashMap<>();

        for (
                AnalysisSkillResult result :
                analysisResults
        ) {

            resultMap.put(
                    result
                            .getSkill()
                            .getId(),
                    result
            );
        }

        List<LearningRecommendationResponse>
                responses =
                learningRepository
                        .findByAnalysisIdOrderByPriorityAsc(
                                analysisId
                        )
                        .stream()
                        .map(recommendation -> {

                            AnalysisSkillResult result =
                                    resultMap.get(
                                            recommendation
                                                    .getSkill()
                                                    .getId()
                                    );

                            return toResponse(
                                    recommendation,
                                    result
                            );
                        })
                        .toList();

        return new LearningPlanResponse(
                analysis.getId(),
                analysis.getJob().getTitle(),
                analysis.getOverallScore(),
                responses
        );
    }

    private int priorityRank(
            AnalysisSkillResult result
    ) {

        boolean required =
                "REQUIRED".equalsIgnoreCase(
                        result.getRequirementLevel()
                );

        boolean missing =
                "MISSING".equals(
                        result.getStatus()
                );

        if (required && missing) {
            return 1;
        }

        if (required) {
            return 2;
        }

        if (missing) {
            return 3;
        }

        return 4;
    }

    private String determinePriorityLevel(
            AnalysisSkillResult result
    ) {

        if (
                "REQUIRED".equalsIgnoreCase(
                        result.getRequirementLevel()
                )
        ) {
            return "HIGH";
        }

        if (
                "MISSING".equals(
                        result.getStatus()
                )
        ) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private String normalizeDifficulty(
            String difficulty
    ) {

        if (difficulty == null) {
            return "MEDIUM";
        }

        String normalized =
                difficulty
                        .trim()
                        .toUpperCase();

        return switch (normalized) {

            case "EASY",
                 "MEDIUM",
                 "HARD"
                    -> normalized;

            default
                    -> "MEDIUM";
        };
    }

    private LearningRecommendationResponse
    toResponse(
            LearningRecommendation recommendation,
            AnalysisSkillResult result
    ) {

        String status =
                result == null
                        ? null
                        : result.getStatus();

        String requirement =
                result == null
                        ? null
                        : result
                                .getRequirementLevel();

        return new LearningRecommendationResponse(
                recommendation.getPriority(),
                recommendation.getPriorityLevel(),
                recommendation
                        .getSkill()
                        .getName(),
                status,
                requirement,
                recommendation.getWhyNeeded(),
                recommendation.getWhatToLearn(),
                recommendation.getLearningSequence(),
                recommendation.getSuggestedProject(),
                recommendation.getDifficulty()
        );
    }
}