package com.devhire.service;

import com.devhire.dto.AnalysisResponse;
import com.devhire.dto.CreateAnalysisRequest;
import com.devhire.dto.SkillMatchResponse;
import com.devhire.entity.*;
import com.devhire.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MatchingService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final JobSkillRepository jobSkillRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisSkillResultRepository resultRepository;
    private final RepositorySkillEvidenceRepository evidenceRepository;

    public MatchingService(
            UserRepository userRepository,
            ResumeRepository resumeRepository,
            JobRepository jobRepository,
            CandidateSkillRepository candidateSkillRepository,
            JobSkillRepository jobSkillRepository,
            AnalysisRepository analysisRepository,
            AnalysisSkillResultRepository resultRepository,
            RepositorySkillEvidenceRepository evidenceRepository
    ) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.candidateSkillRepository = candidateSkillRepository;
        this.jobSkillRepository = jobSkillRepository;
        this.analysisRepository = analysisRepository;
        this.resultRepository = resultRepository;
        this.evidenceRepository = evidenceRepository;
    }

    @Transactional
    public AnalysisResponse analyze(
            CreateAnalysisRequest request,
            String userEmail
    ) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        Resume resume = resumeRepository
                .findByIdAndUserId(
                        request.getResumeId(),
                        user.getId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Resume not found"
                        )
                );

        Job job = jobRepository
                .findByIdAndUserId(
                        request.getJobId(),
                        user.getId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Job not found"
                        )
                );

        List<CandidateSkill> candidateSkills =
                candidateSkillRepository
                        .findByResumeId(resume.getId());

        List<JobSkill> jobSkills =
                jobSkillRepository
                        .findByJobId(job.getId());

        if (jobSkills.isEmpty()) {
            throw new IllegalArgumentException(
                    "This job has no analyzed skills"
            );
        }

        /*
         * Candidate skills:
         * normalized skill -> strongest resume evidence
         */

        Map<String, CandidateSkill> candidateMap =
                new HashMap<>();

        for (CandidateSkill candidate : candidateSkills) {

            String normalized =
                    candidate
                            .getSkill()
                            .getNormalizedName();

            CandidateSkill existing =
                    candidateMap.get(normalized);

            if (
                    existing == null ||
                    candidate.getConfidence()
                            > existing.getConfidence()
            ) {
                candidateMap.put(
                        normalized,
                        candidate
                );
            }
        }

        /*
         * GitHub evidence:
         * normalized skill -> strongest repository evidence
         */

        List<RepositorySkillEvidence> githubEvidence =
                evidenceRepository
                        .findByRepository_User_Id(
                                user.getId()
                        );

        Map<String, RepositorySkillEvidence>
                githubMap = new HashMap<>();

        for (RepositorySkillEvidence evidence :
                githubEvidence) {

            String normalized =
                    evidence
                            .getSkill()
                            .getNormalizedName();

            RepositorySkillEvidence existing =
                    githubMap.get(normalized);

            if (
                    existing == null ||
                    evidence.getConfidence()
                            > existing.getConfidence()
            ) {

                githubMap.put(
                        normalized,
                        evidence
                );
            }
        }

        List<TemporaryMatch> matches =
                new ArrayList<>();

        double requiredEarned = 0;
        double requiredPossible = 0;

        double preferredEarned = 0;
        double preferredPossible = 0;

        double githubEarned = 0;
        double githubPossible = 0;

        for (JobSkill jobSkill : jobSkills) {

            Skill requiredSkill =
                    jobSkill.getSkill();

            String normalized =
                    requiredSkill
                            .getNormalizedName();

            CandidateSkill candidate =
                    candidateMap.get(normalized);

            RepositorySkillEvidence github =
                    githubMap.get(normalized);

            String status;

            Double candidateConfidence = null;
            Double githubConfidence = null;
            String githubLevel = "NO_EVIDENCE";

            double resumeCredit = 0;

            /*
             * Resume evidence
             */

            if (candidate != null) {

                candidateConfidence =
                        candidate.getConfidence();

                if (candidateConfidence >= 0.65) {

                    status = "MATCHED";

                    resumeCredit =
                            candidateConfidence;

                } else {

                    status = "PARTIAL";

                    resumeCredit =
                            candidateConfidence * 0.5;
                }

            } else {

                CandidateSkill related =
                        findRelatedSkill(
                                normalized,
                                candidateMap
                        );

                if (related != null) {

                    status = "PARTIAL";

                    candidateConfidence =
                            related.getConfidence();

                    resumeCredit =
                            candidateConfidence * 0.5;

                } else {

                    status = "MISSING";
                }
            }

            /*
             * GitHub evidence
             */

            if (github != null) {

                githubConfidence =
                        github.getConfidence();

                githubLevel =
                        github.getEvidenceLevel();

                /*
                 * If resume did not demonstrate the
                 * skill but GitHub has meaningful
                 * evidence, we label it PARTIAL,
                 * not MATCHED.
                 */
                if (
                        status.equals("MISSING") &&
                        githubConfidence >= 0.60
                ) {

                    status = "PARTIAL";
                }
            }

            boolean preferred =
                    "PREFERRED".equalsIgnoreCase(
                            jobSkill.getRequirementLevel()
                    );

            if (preferred) {

                preferredPossible += 1;
                preferredEarned += resumeCredit;

            } else {

                requiredPossible += 1;
                requiredEarned += resumeCredit;
            }

            /*
             * GitHub evidence is a bonus only.
             *
             * Required skills count more heavily
             * than preferred skills.
             */

            double githubWeight =
                    preferred ? 0.5 : 1.0;

            githubPossible += githubWeight;

            if (githubConfidence != null) {

                githubEarned +=
                        githubConfidence
                                * githubWeight;
            }

            matches.add(
                    new TemporaryMatch(
                            requiredSkill,
                            jobSkill.getRequirementLevel(),
                            status,
                            candidateConfidence,
                            githubLevel,
                            githubConfidence
                    )
            );
        }

        double requiredScore =
                requiredPossible == 0
                        ? 0
                        : requiredEarned
                        / requiredPossible;

        double preferredScore =
                preferredPossible == 0
                        ? 0
                        : preferredEarned
                        / preferredPossible;

        double baseScore;

        if (
                requiredPossible > 0 &&
                preferredPossible > 0
        ) {

            baseScore =
                    (
                            requiredScore * 0.80
                                    +
                            preferredScore * 0.20
                    ) * 100;

        } else if (requiredPossible > 0) {

            baseScore =
                    requiredScore * 100;

        } else {

            baseScore =
                    preferredScore * 100;
        }

        /*
         * Maximum GitHub bonus = 10 points.
         *
         * No GitHub evidence does NOT reduce
         * the base score.
         */

        double githubBonus =
                githubPossible == 0
                        ? 0
                        :
                        (
                                githubEarned
                                        / githubPossible
                        ) * 10;

        baseScore =
                roundOneDecimal(baseScore);

        githubBonus =
                roundOneDecimal(githubBonus);

        double overallScore =
                Math.min(
                        100,
                        baseScore + githubBonus
                );

        overallScore =
                roundOneDecimal(overallScore);

        Analysis analysis =
                analysisRepository.save(
                        new Analysis(
                                user,
                                resume,
                                job,
                                baseScore,
                                githubBonus,
                                overallScore
                        )
                );

        List<SkillMatchResponse> responses =
                new ArrayList<>();

        int matched = 0;
        int partial = 0;
        int missing = 0;

        for (TemporaryMatch match : matches) {

            resultRepository.save(
                    new AnalysisSkillResult(
                            analysis,
                            match.skill(),
                            match.status(),
                            match.requirementLevel(),
                            match.candidateConfidence(),
                            match.githubEvidenceLevel(),
                            match.githubConfidence()
                    )
            );

            switch (match.status()) {

                case "MATCHED" -> matched++;

                case "PARTIAL" -> partial++;

                default -> missing++;
            }

            responses.add(
                    new SkillMatchResponse(
                            match.skill().getName(),
                            match.requirementLevel(),
                            match.status(),
                            match.candidateConfidence(),
                            match.githubEvidenceLevel(),
                            match.githubConfidence()
                    )
            );
        }

        return new AnalysisResponse(
                analysis.getId(),
                baseScore,
                githubBonus,
                overallScore,
                matched,
                partial,
                missing,
                responses
        );
    }

    private CandidateSkill findRelatedSkill(
            String target,
            Map<String, CandidateSkill> candidates
    ) {

        Map<String, Set<String>> relationships =
                Map.of(
                        "spring boot",
                        Set.of("spring"),

                        "spring",
                        Set.of("spring boot"),

                        "javascript",
                        Set.of("typescript"),

                        "typescript",
                        Set.of("javascript"),

                        "postgresql",
                        Set.of("sql"),

                        "mysql",
                        Set.of("sql")
                );

        Set<String> related =
                relationships.get(target);

        if (related == null) {
            return null;
        }

        for (String name : related) {

            CandidateSkill candidate =
                    candidates.get(name);

            if (candidate != null) {
                return candidate;
            }
        }

        return null;
    }

    private double roundOneDecimal(
            double value
    ) {

        return Math.round(
                value * 10.0
        ) / 10.0;
    }

    private record TemporaryMatch(
            Skill skill,
            String requirementLevel,
            String status,
            Double candidateConfidence,
            String githubEvidenceLevel,
            Double githubConfidence
    ) {
    }
}