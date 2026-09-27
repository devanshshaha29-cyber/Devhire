package com.devhire.controller;

import com.devhire.dto.CreateJobRequest;
import com.devhire.dto.JobResponse;
import com.devhire.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody CreateJobRequest request,
            Authentication authentication
    ) {

        JobResponse response =
                jobService.createJob(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getJobs(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                jobService.getUserJobs(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(
            @PathVariable Long id,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                jobService.getJob(
                        id,
                        authentication.getName()
                )
        );
    }
}