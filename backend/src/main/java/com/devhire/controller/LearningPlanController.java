package com.devhire.controller;

import com.devhire.dto.LearningPlanResponse;
import com.devhire.service.LearningPlanService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learning-plan")
public class LearningPlanController {

    private final LearningPlanService
            learningPlanService;

    public LearningPlanController(
            LearningPlanService learningPlanService
    ) {
        this.learningPlanService =
                learningPlanService;
    }

    @PostMapping("/{analysisId}")
    public ResponseEntity<LearningPlanResponse>
    generate(
            @PathVariable Long analysisId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                learningPlanService.generate(
                        analysisId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<LearningPlanResponse>
    get(
            @PathVariable Long analysisId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                learningPlanService.getExisting(
                        analysisId,
                        authentication.getName()
                )
        );
    }
}