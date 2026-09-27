package com.devhire.repository;

import com.devhire.entity.CandidateSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateSkillRepository
        extends JpaRepository<CandidateSkill, Long> {

    List<CandidateSkill> findByResumeId(Long resumeId);

    void deleteByResumeId(Long resumeId);
}