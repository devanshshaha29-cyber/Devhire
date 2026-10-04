package com.devhire.service;

import com.devhire.dto.AiLearningPlanResult;
import com.devhire.dto.SkillExtractionResult;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class GeminiAiProvider implements AiProvider {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.model}")
    private String model;

    @Value("${ai.fallback-model:gemini-3.5-flash-lite}")
    private String fallbackModel;

    public GeminiAiProvider(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.restClient = RestClient.builder()
                .baseUrl(
                        "https://generativelanguage.googleapis.com/v1beta"
                )
                .build();
    }

    @Override
    public SkillExtractionResult extractResumeSkills(
            String resumeText
    ) {

        String prompt = """
                You are analyzing a software developer resume.

                Extract only technical skills clearly supported
                by the resume.

                Do not invent skills.

                Possible categories:
                Programming Language
                Backend Framework
                Frontend Framework
                Database
                Cloud
                DevOps
                Testing
                Tool
                Other Technical Skill

                Confidence must be between 0 and 1.

                requirementLevel must always be CANDIDATE.

                Resume:

                """ + resumeText;

        return callGemini(
                prompt,
                createSkillSchema(),
                SkillExtractionResult.class
        );
    }

    @Override
    public SkillExtractionResult extractJobSkills(
            String jobDescription
    ) {

        String prompt = """
                Analyze this software engineering job description.

                Extract technical skills only.

                requirementLevel must be either:
                REQUIRED
                or
                PREFERRED.

                Do not invent skills.

                Confidence must be between 0 and 1.

                Job description:

                """ + jobDescription;

        return callGemini(
                prompt,
                createSkillSchema(),
                SkillExtractionResult.class
        );
    }

    @Override
    public AiLearningPlanResult generateLearningPlan(
            String jobTitle,
            double overallScore,
            List<String> prioritySkills
    ) {

        String skills =
                String.join("\n", prioritySkills);

        String prompt = """
                You are creating a personalized technical
                learning roadmap for a junior software developer.

                Target job:
                %s

                Current DevHire match score:
                %.1f%%

                Skill gaps, already prioritized by DevHire:

                %s

                Create exactly one recommendation for each skill
                listed above.

                Do not introduce additional skills.

                For every recommendation provide:

                skill
                whyNeeded
                whatToLearn
                learningSequence
                suggestedProject
                difficulty

                Keep the advice practical and specific to the
                target job.

                learningSequence should be concise and ordered.

                suggestedProject should demonstrate the skill
                practically.

                difficulty must be one of:
                EASY
                MEDIUM
                HARD.
                """
                .formatted(
                        jobTitle,
                        overallScore,
                        skills
                );

        return callGemini(
                prompt,
                createLearningPlanSchema(),
                AiLearningPlanResult.class
        );
    }

    private <T> T callGemini(
            String prompt,
            Map<String, Object> schema,
            Class<T> responseType
    ) {

        try {

            JsonNode response;

            try {
                response = performGeminiRequest(
                        model,
                        prompt,
                        schema
                );
            } catch (RestClientResponseException exception) {

                if (
                        exception.getStatusCode().value() == 503 &&
                        fallbackModel != null &&
                        !fallbackModel.isBlank() &&
                        !fallbackModel.equals(model)
                ) {
                    response = performGeminiRequest(
                            fallbackModel,
                            prompt,
                            schema
                    );
                } else {
                    throw exception;
                }
            }

            String jsonText =
                    findOutputText(response);

            return objectMapper.readValue(
                    jsonText,
                    responseType
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "AI analysis failed: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    private JsonNode performGeminiRequest(
            String selectedModel,
            String prompt,
            Map<String, Object> schema
    ) {

        Map<String, Object> responseFormat =
                Map.of(
                        "type", "text",
                        "mime_type", "application/json",
                        "schema", schema
                );

        Map<String, Object> requestBody =
                Map.of(
                        "model", selectedModel,
                        "input", prompt,
                        "response_format", responseFormat,
                        "store", false
                );

        return restClient
                .post()
                .uri("/interactions")
                .header(
                        "x-goog-api-key",
                        apiKey
                )
                .contentType(
                        MediaType.APPLICATION_JSON
                )
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);
    }

    private Map<String, Object> createSkillSchema() {

        Map<String, Object> skill =
                Map.of(
                        "type", "object",

                        "properties", Map.of(
                                "name",
                                Map.of("type", "string"),

                                "category",
                                Map.of("type", "string"),

                                "confidence",
                                Map.of(
                                        "type", "number",
                                        "minimum", 0,
                                        "maximum", 1
                                ),

                                "requirementLevel",
                                Map.of("type", "string")
                        ),

                        "required",
                        List.of(
                                "name",
                                "category",
                                "confidence",
                                "requirementLevel"
                        )
                );

        return Map.of(
                "type", "object",

                "properties",
                Map.of(
                        "skills",
                        Map.of(
                                "type", "array",
                                "items", skill
                        )
                ),

                "required",
                List.of("skills")
        );
    }

    private Map<String, Object>
    createLearningPlanSchema() {

        Map<String, Object> recommendation =
                Map.of(
                        "type", "object",

                        "properties", Map.of(
                                "skill",
                                Map.of("type", "string"),

                                "whyNeeded",
                                Map.of("type", "string"),

                                "whatToLearn",
                                Map.of("type", "string"),

                                "learningSequence",
                                Map.of("type", "string"),

                                "suggestedProject",
                                Map.of("type", "string"),

                                "difficulty",
                                Map.of("type", "string")
                        ),

                        "required",
                        List.of(
                                "skill",
                                "whyNeeded",
                                "whatToLearn",
                                "learningSequence",
                                "suggestedProject",
                                "difficulty"
                        )
                );

        return Map.of(
                "type", "object",

                "properties",
                Map.of(
                        "recommendations",
                        Map.of(
                                "type", "array",
                                "items", recommendation
                        )
                ),

                "required",
                List.of("recommendations")
        );
    }

    private String findOutputText(
            JsonNode response
    ) {

        if (response == null) {
            throw new IllegalStateException(
                    "Gemini returned an empty response"
            );
        }

        JsonNode steps =
                response.path("steps");

        if (!steps.isArray()) {
            throw new IllegalStateException(
                    "Gemini response does not contain steps"
            );
        }

        for (JsonNode step : steps) {

            JsonNode content =
                    step.path("content");

            if (!content.isArray()) {
                continue;
            }

            for (JsonNode item : content) {

                String text =
                        item
                                .path("text")
                                .asString(null);

                if (
                        text != null &&
                        !text.isBlank()
                ) {
                    return text;
                }
            }
        }

        throw new IllegalStateException(
                "Gemini response did not contain output text"
        );
    }
}
