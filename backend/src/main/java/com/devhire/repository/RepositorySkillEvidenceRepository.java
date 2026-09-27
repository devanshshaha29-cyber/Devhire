package com.devhire.repository;

import com.devhire.entity.RepositorySkillEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositorySkillEvidenceRepository
        extends JpaRepository<RepositorySkillEvidence, Long> {

    List<RepositorySkillEvidence>
    findByRepositoryId(Long repositoryId);

    List<RepositorySkillEvidence>
    findByRepository_User_Id(Long userId);

    void deleteByRepositoryId(Long repositoryId);
}