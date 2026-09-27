package com.devhire.service;

import com.devhire.dto.*;
import com.devhire.entity.*;
import com.devhire.repository.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.*;

@Service
public class GitHubService {

    private final RestClient restClient;

    private final UserRepository userRepository;
    private final CodeRepositoryRepository codeRepositoryRepository;
    private final RepositorySkillEvidenceRepository evidenceRepository;
    private final SkillRepository skillRepository;
    private final SkillNormalizationService normalizationService;

    @Value("${github.token:}")
    private String githubToken;

    public GitHubService(
            UserRepository userRepository,
            CodeRepositoryRepository codeRepositoryRepository,
            RepositorySkillEvidenceRepository evidenceRepository,
            SkillRepository skillRepository,
            SkillNormalizationService normalizationService
    ) {
        this.userRepository = userRepository;
        this.codeRepositoryRepository =
                codeRepositoryRepository;
        this.evidenceRepository =
                evidenceRepository;
        this.skillRepository =
                skillRepository;
        this.normalizationService =
                normalizationService;

        this.restClient =
                RestClient.builder()
                        .baseUrl("https://api.github.com")
                        .build();
    }

    @Transactional
    public List<GithubRepositoryResponse> getRepositories(
            String username,
            String userEmail
    ) {

        validateUsername(username);

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        GitHubApiRepository[] repositories;

        try {

            repositories =
                    restClient
                            .get()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .path(
                                                    "/users/{username}/repos"
                                            )
                                            .queryParam(
                                                    "type",
                                                    "owner"
                                            )
                                            .queryParam(
                                                    "sort",
                                                    "updated"
                                            )
                                            .queryParam(
                                                    "per_page",
                                                    30
                                            )
                                            .build(username)
                            )
                            .headers(
                                    headers ->
                                            addHeaders(
                                                    headers,
                                                    "application/vnd.github+json"
                                            )
                            )
                            .retrieve()
                            .body(
                                    GitHubApiRepository[].class
                            );

        } catch (RestClientResponseException exception) {

    if (exception.getStatusCode().value() == 404) {

        throw new IllegalArgumentException(
                "GitHub username not found."
        );
    }

    if (exception.getStatusCode().value() == 403) {

        throw new IllegalStateException(
                "GitHub API rate limit reached. Please try again later."
        );
    }

    if (exception.getStatusCode().value() == 401) {

        throw new IllegalStateException(
                "GitHub authentication failed."
        );
    }

    throw new IllegalStateException(
            "GitHub is temporarily unavailable. Please try again later."
    );
}

        if (repositories == null) {
            return List.of();
        }

        List<GithubRepositoryResponse> responses =
                new ArrayList<>();

        for (GitHubApiRepository apiRepo :
                repositories) {

            CodeRepository repository =
                    codeRepositoryRepository
                            .findByUserIdAndGithubRepositoryId(
                                    user.getId(),
                                    apiRepo.id()
                            )
                            .orElseGet(() ->
                                    new CodeRepository(
                                            user,
                                            apiRepo.id(),
                                            username
                                    )
                            );

            List<String> topics =
                    apiRepo.topics() == null
                            ? List.of()
                            : apiRepo.topics();

            repository.updateMetadata(
                    apiRepo.name(),
                    apiRepo.full_name(),
                    apiRepo.description(),
                    apiRepo.language(),
                    safeInteger(
                            apiRepo.stargazers_count()
                    ),
                    safeInteger(
                            apiRepo.forks_count()
                    ),
                    String.join(",", topics),
                    apiRepo.html_url(),
                    apiRepo.updated_at(),
                    Boolean.TRUE.equals(
                            apiRepo.fork()
                    )
            );

            repository =
                    codeRepositoryRepository.save(
                            repository
                    );

            responses.add(
                    toRepositoryResponse(
                            repository
                    )
            );
        }

        return responses;
    }

    @Transactional
    public GithubAnalysisResponse analyzeRepositories(
            String username,
            String userEmail
    ) {

        getRepositories(
                username,
                userEmail
        );

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow();

        List<CodeRepository> repositories =
                codeRepositoryRepository
                        .findByUserIdAndGithubUsernameOrderByGithubUpdatedAtDesc(
                                user.getId(),
                                username
                        );

        List<GithubRepositoryEvidenceResponse>
                results = new ArrayList<>();

        int analyzedCount = 0;

        /*
         * Limit analysis to 10 recent repositories.
         * This controls GitHub API usage and future AI cost.
         */
        for (CodeRepository repository :
                repositories.stream()
                        .limit(5)
                        .toList()) {

            /*
             * We display forks,
             * but do not use forked projects
             * as proof of skill.
             */
            if (Boolean.TRUE.equals(
                    repository.getForked()
            )) {
                continue;
            }

            analyzedCount++;

            evidenceRepository
                    .deleteByRepositoryId(
                            repository.getId()
                    );
                    evidenceRepository.flush();

            Map<String, EvidenceAccumulator>
                    accumulated = new HashMap<>();

            analyzePrimaryLanguage(
                    repository,
                    accumulated
            );

            analyzeTopics(
                    repository,
                    accumulated
            );

            String readme =
                    fetchReadme(
                            repository.getGithubUsername(),
                            repository.getName()
                    );

            if (readme != null) {

                detectTechnologySignals(
                        readme,
                        "README",
                        0.30,
                        accumulated
                );
            }

            analyzeDependencyFiles(
                    repository,
                    accumulated
            );

            List<SkillEvidenceResponse>
                    responseEvidence =
                    saveEvidence(
                            repository,
                            accumulated
                    );

            repository.markAnalyzed();

            codeRepositoryRepository.save(
                    repository
            );

            results.add(
                    new GithubRepositoryEvidenceResponse(
                            repository.getId(),
                            repository.getName(),
                            repository.getPrimaryLanguage(),
                            repository.getGithubUrl(),
                            responseEvidence
                    )
            );
        }

        return new GithubAnalysisResponse(
                username,
                repositories.size(),
                analyzedCount,
                results
        );
    }

    private void analyzePrimaryLanguage(
            CodeRepository repository,
            Map<String, EvidenceAccumulator> accumulated
    ) {

        String language =
                repository.getPrimaryLanguage();

        if (language == null ||
                language.isBlank()) {
            return;
        }

        addSignal(
                accumulated,
                language,
                "Programming Language",
                0.78,
                "GitHub identifies this as the repository's primary language"
        );
    }

    private void analyzeTopics(
            CodeRepository repository,
            Map<String, EvidenceAccumulator> accumulated
    ) {

        if (repository.getTopics() == null ||
                repository.getTopics().isBlank()) {
            return;
        }

        String[] topics =
                repository
                        .getTopics()
                        .split(",");

        for (String topic : topics) {

            detectKnownTopic(
                    topic.trim(),
                    accumulated
            );
        }
    }

    private void detectKnownTopic(
            String topic,
            Map<String, EvidenceAccumulator> accumulated
    ) {

        String lower =
                topic.toLowerCase();

        switch (lower) {

            case "spring-boot" ->
                    addSignal(
                            accumulated,
                            "Spring Boot",
                            "Backend Framework",
                            0.55,
                            "Repository topic includes Spring Boot"
                    );

            case "react", "reactjs" ->
                    addSignal(
                            accumulated,
                            "React",
                            "Frontend Framework",
                            0.55,
                            "Repository topic includes React"
                    );

            case "docker" ->
                    addSignal(
                            accumulated,
                            "Docker",
                            "DevOps",
                            0.55,
                            "Repository topic includes Docker"
                    );

            case "postgresql", "postgres" ->
                    addSignal(
                            accumulated,
                            "PostgreSQL",
                            "Database",
                            0.55,
                            "Repository topic includes PostgreSQL"
                    );

            case "redis" ->
                    addSignal(
                            accumulated,
                            "Redis",
                            "Database",
                            0.55,
                            "Repository topic includes Redis"
                    );

            case "kafka" ->
                    addSignal(
                            accumulated,
                            "Kafka",
                            "Other Technical Skill",
                            0.55,
                            "Repository topic includes Kafka"
                    );

            case "typescript" ->
                    addSignal(
                            accumulated,
                            "TypeScript",
                            "Programming Language",
                            0.55,
                            "Repository topic includes TypeScript"
                    );

            case "nodejs", "node-js" ->
                    addSignal(
                            accumulated,
                            "Node.js",
                            "Backend Framework",
                            0.55,
                            "Repository topic includes Node.js"
                    );

            case "mongodb" ->
                    addSignal(
                            accumulated,
                            "MongoDB",
                            "Database",
                            0.55,
                            "Repository topic includes MongoDB"
                    );
        }
    }

    private void analyzeDependencyFiles(
            CodeRepository repository,
            Map<String, EvidenceAccumulator> accumulated
    ) {

        List<String> files =
                List.of(
                        "pom.xml",
                        "build.gradle",
                        "package.json",
                        "requirements.txt",
                        "pyproject.toml",
                        "Dockerfile",
                        "docker-compose.yml",
                        "docker-compose.yaml"
                );

        for (String file : files) {

            String content =
                    fetchRepositoryFile(
                            repository.getGithubUsername(),
                            repository.getName(),
                            file
                    );

            if (content == null ||
                    content.isBlank()) {
                continue;
            }

            if (
                    file.equals("Dockerfile") ||
                    file.startsWith(
                            "docker-compose"
                    )
            ) {

                addSignal(
                        accumulated,
                        "Docker",
                        "DevOps",
                        0.92,
                        file + " exists in the repository"
                );
            }

            detectTechnologySignals(
                    content,
                    file,
                    0.90,
                    accumulated
            );
        }
    }

    private void detectTechnologySignals(
            String text,
            String source,
            double confidence,
            Map<String, EvidenceAccumulator> accumulated
    ) {

        String lower =
                text.toLowerCase();

        addIfContains(
                lower,
                List.of(
                        "spring-boot",
                        "springframework.boot"
                ),
                accumulated,
                "Spring Boot",
                "Backend Framework",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of(
                        "\"react\"",
                        "reactjs"
                ),
                accumulated,
                "React",
                "Frontend Framework",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of("typescript"),
                accumulated,
                "TypeScript",
                "Programming Language",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of(
                        "postgresql",
                        "postgres"
                ),
                accumulated,
                "PostgreSQL",
                "Database",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of("mongodb"),
                accumulated,
                "MongoDB",
                "Database",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of("redis"),
                accumulated,
                "Redis",
                "Database",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of("kafka"),
                accumulated,
                "Kafka",
                "Other Technical Skill",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of(
                        "\"express\"",
                        "expressjs"
                ),
                accumulated,
                "Express",
                "Backend Framework",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of(
                        "node.js",
                        "nodejs"
                ),
                accumulated,
                "Node.js",
                "Backend Framework",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of("tailwind"),
                accumulated,
                "Tailwind CSS",
                "Frontend Framework",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of("django"),
                accumulated,
                "Django",
                "Backend Framework",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of("fastapi"),
                accumulated,
                "FastAPI",
                "Backend Framework",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of("flask"),
                accumulated,
                "Flask",
                "Backend Framework",
                confidence,
                source
        );

        addIfContains(
                lower,
                List.of(
                        "amazon web services",
                        "\"aws\""
                ),
                accumulated,
                "AWS",
                "Cloud",
                confidence,
                source
        );
    }

    private void addIfContains(
            String text,
            List<String> indicators,
            Map<String, EvidenceAccumulator> accumulated,
            String skill,
            String category,
            double confidence,
            String source
    ) {

        for (String indicator :
                indicators) {

            if (text.contains(indicator)) {

                addSignal(
                        accumulated,
                        skill,
                        category,
                        confidence,
                        source +
                                " contains technology indicator for "
                                + skill
                );

                return;
            }
        }
    }

    private void addSignal(
            Map<String, EvidenceAccumulator> accumulated,
            String skillName,
            String category,
            double confidence,
            String reason
    ) {

        String normalized =
                normalizationService.normalize(
                        skillName
                );

        EvidenceAccumulator accumulator =
                accumulated.computeIfAbsent(
                        normalized,
                        ignored ->
                                new EvidenceAccumulator(
                                        skillName,
                                        category
                                )
                );

        accumulator.add(
                confidence,
                reason
        );
    }

    private List<SkillEvidenceResponse> saveEvidence(
            CodeRepository repository,
            Map<String, EvidenceAccumulator> accumulated
    ) {

        List<SkillEvidenceResponse> responses =
                new ArrayList<>();

        for (
                Map.Entry<
                        String,
                        EvidenceAccumulator
                > entry :
                accumulated.entrySet()
        ) {

            EvidenceAccumulator accumulator =
                    entry.getValue();

            double confidence =
                    Math.min(
                            0.99,
                            accumulator.confidence
                    );

            if (confidence < 0.25) {
                continue;
            }

            Skill skill =
                    skillRepository
                            .findByNormalizedName(
                                    entry.getKey()
                            )
                            .orElseGet(() ->
                                    skillRepository.save(
                                            new Skill(
                                                    accumulator.skillName,
                                                    entry.getKey(),
                                                    accumulator.category
                                            )
                                    )
                            );

            String evidenceLevel =
                    evidenceLevel(confidence);

            String summary =
                    String.join(
                            "; ",
                            accumulator.reasons
                    );

            evidenceRepository.save(
                    new RepositorySkillEvidence(
                            repository,
                            skill,
                            evidenceLevel,
                            confidence,
                            summary
                    )
            );

            responses.add(
                    new SkillEvidenceResponse(
                            skill.getName(),
                            evidenceLevel,
                            Math.round(
                                    confidence * 100.0
                            ) / 100.0,
                            summary
                    )
            );
        }

        responses.sort(
                Comparator.comparing(
                        SkillEvidenceResponse::confidence
                ).reversed()
        );

        return responses;
    }

    private String evidenceLevel(
            double confidence
    ) {

        if (confidence >= 0.80) {
            return "STRONG";
        }

        if (confidence >= 0.60) {
            return "MODERATE";
        }

        return "WEAK";
    }

    private String fetchReadme(
            String owner,
            String repository
    ) {

        return fetchRaw(
                "/repos/{owner}/{repository}/readme",
                owner,
                repository
        );
    }

    private String fetchRepositoryFile(
            String owner,
            String repository,
            String file
    ) {

        return fetchRaw(
                "/repos/{owner}/{repository}/contents/"
                        + file,
                owner,
                repository
        );
    }

    private String fetchRaw(
            String path,
            String owner,
            String repository
    ) {

        try {

            return restClient
                    .get()
                    .uri(
                            path,
                            owner,
                            repository
                    )
                    .headers(
                            headers ->
                                    addHeaders(
                                            headers,
                                            "application/vnd.github.raw+json"
                                    )
                    )
                    .retrieve()
                    .body(String.class);

        } catch (RestClientResponseException exception) {

            if (
                    exception
                            .getStatusCode()
                            .value()
                            == 404
            ) {
                return null;
            }

            return null;
        }
    }

    private void addHeaders(
            HttpHeaders headers,
            String accept
    ) {

        headers.set(
                "Accept",
                accept
        );

        headers.set(
                "User-Agent",
                "DevHire"
        );

        headers.set(
                "X-GitHub-Api-Version",
                "2026-03-10"
        );

        if (
                githubToken != null &&
                !githubToken.isBlank()
        ) {

            headers.setBearerAuth(
                    githubToken
            );
        }
    }

    private void validateUsername(
            String username
    ) {

        if (
                username == null ||
                !username.matches(
                        "^[A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])?$"
                )
        ) {

            throw new IllegalArgumentException(
                    "Invalid GitHub username"
            );
        }
    }

    private GithubRepositoryResponse
    toRepositoryResponse(
            CodeRepository repository
    ) {

        List<String> topics =
                repository.getTopics() == null ||
                        repository
                                .getTopics()
                                .isBlank()
                        ? List.of()
                        : Arrays.asList(
                                repository
                                        .getTopics()
                                        .split(",")
                        );

        return new GithubRepositoryResponse(
                repository.getId(),
                repository.getName(),
                repository.getDescription(),
                repository.getPrimaryLanguage(),
                repository.getStars(),
                repository.getForks(),
                topics,
                repository.getGithubUrl(),
                repository.getGithubUpdatedAt(),
                repository.getForked()
        );
    }

    private int safeInteger(
            Integer value
    ) {
        return value == null
                ? 0
                : value;
    }

    private static class EvidenceAccumulator {

        private final String skillName;
        private final String category;

        private double confidence = 0;

        private final List<String> reasons =
                new ArrayList<>();

        private EvidenceAccumulator(
                String skillName,
                String category
        ) {
            this.skillName = skillName;
            this.category = category;
        }

        private void add(
                double signal,
                String reason
        ) {

            /*
             * Multiple independent signals increase
             * confidence without simply adding them.
             */
            confidence =
                    1 -
                    (
                            (1 - confidence)
                            *
                            (1 - signal)
                    );

            reasons.add(reason);
        }
    }
}