package com.devhire.controller;

import com.devhire.dto.AnalysisResponse;
import com.devhire.dto.CreateAnalysisRequest;
import com.devhire.service.MatchingService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final MatchingService matchingService;

    public AnalysisController(
            MatchingService matchingService
    ) {
        this.matchingService =
                matchingService;
    }

    @PostMapping
    public ResponseEntity<AnalysisResponse>
    analyze(
            @Valid
            @RequestBody
            CreateAnalysisRequest request,

            Authentication authentication
    ) {

        return ResponseEntity.ok(
                matchingService.analyze(
                        request,
                        authentication.getName()
                )
        );
    }
}