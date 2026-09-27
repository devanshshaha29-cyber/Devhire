package com.devhire.service;

import com.devhire.dto.ExtractedSkill;
import com.devhire.entity.*;
import com.devhire.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final JobSkillRepository jobSkillRepository;
    private final SkillNormalizationService normalizationService;

    public SkillService(
            SkillRepository skillRepository,
            CandidateSkillRepository candidateSkillRepository,
            JobSkillRepository jobSkillRepository,
            SkillNormalizationService normalizationService
    ) {
        this.skillRepository = skillRepository;
        this.candidateSkillRepository =
                candidateSkillRepository;
        this.jobSkillRepository =
                jobSkillRepository;
        this.normalizationService =
                normalizationService;
    }

    private Skill findOrCreate(
            ExtractedSkill extracted
    ) {

        String normalized =
                normalizationService.normalize(
                        extracted.getName()
                );

        return skillRepository
                .findByNormalizedName(normalized)
                .orElseGet(() ->
                        skillRepository.save(
                                new Skill(
                                        extracted.getName().trim(),
                                        normalized,
                                        extracted.getCategory()
                                )
                        )
                );
    }

    @Transactional
    public void saveCandidateSkills(
            Resume resume,
            Iterable<ExtractedSkill> extractedSkills
    ) {

        candidateSkillRepository
                .deleteByResumeId(resume.getId());

        for (ExtractedSkill extracted :
                extractedSkills) {

            if (extracted.getConfidence() < 0.40) {
                continue;
            }

            Skill skill =
                    findOrCreate(extracted);

            candidateSkillRepository.save(
                    new CandidateSkill(
                            resume,
                            skill,
                            extracted.getConfidence()
                    )
            );
        }
    }

    @Transactional
    public void saveJobSkills(
            Job job,
            Iterable<ExtractedSkill> extractedSkills
    ) {

        jobSkillRepository
                .deleteByJobId(job.getId());

        for (ExtractedSkill extracted :
                extractedSkills) {

            if (extracted.getConfidence() < 0.40) {
                continue;
            }

            Skill skill =
                    findOrCreate(extracted);

            String level =
                    "PREFERRED".equalsIgnoreCase(
                            extracted.getRequirementLevel()
                    )
                            ? "PREFERRED"
                            : "REQUIRED";

            jobSkillRepository.save(
                    new JobSkill(
                            job,
                            skill,
                            level,
                            extracted.getConfidence()
                    )
            );
        }
    }
}