package com.devhire.controller;

import com.devhire.dto.ResumeUploadResponse;
import com.devhire.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.devhire.dto.ResumeSummaryResponse;
import java.util.List;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
        @GetMapping
public ResponseEntity<List<ResumeSummaryResponse>>
getResumes(
        Authentication authentication
) {

    return ResponseEntity.ok(
            resumeService.listResumes(
                    authentication.getName()
            )
    );
}

    private final ResumeService resumeService;

    public ResumeController(
            ResumeService resumeService
    ) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {

        try {

            ResumeUploadResponse response =
                    resumeService.uploadResume(
                            file,
                            authentication.getName()
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    exception.getMessage()
                            )
                    );

        } catch (IOException exception) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "message",
                                    "Unable to read this resume file"
                            )
                    );
        }
    }
}