package com.devhire.repository;

import com.devhire.entity.CodeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CodeRepositoryRepository
        extends JpaRepository<CodeRepository, Long> {

    Optional<CodeRepository>
    findByUserIdAndGithubRepositoryId(
            Long userId,
            Long githubRepositoryId
    );

    List<CodeRepository>
    findByUserIdAndGithubUsernameOrderByGithubUpdatedAtDesc(
            Long userId,
            String githubUsername
    );
}