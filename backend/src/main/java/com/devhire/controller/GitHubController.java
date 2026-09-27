package com.devhire.controller;

import com.devhire.dto.GithubAnalysisResponse;
import com.devhire.dto.GithubRepositoryResponse;
import com.devhire.service.GitHubService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {
    @GetMapping("/ping")
public ResponseEntity<String> ping() {
    return ResponseEntity.ok("GitHub controller is working");
}

    private final GitHubService gitHubService;

    public GitHubController(
            GitHubService gitHubService
    ) {
        this.gitHubService =
                gitHubService;
    }

    @GetMapping("/{username}/repositories")
    public ResponseEntity<
            List<GithubRepositoryResponse>
    > getRepositories(
            @PathVariable String username,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                gitHubService.getRepositories(
                        username,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{username}/analyze")
    public ResponseEntity<
            GithubAnalysisResponse
    > analyzeRepositories(
            @PathVariable String username,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                gitHubService.analyzeRepositories(
                        username,
                        authentication.getName()
                )
        );
    }
}