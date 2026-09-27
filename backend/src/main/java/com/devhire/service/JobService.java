package com.devhire.service;

import com.devhire.dto.CreateJobRequest;
import com.devhire.dto.JobResponse;
import com.devhire.entity.Job;
import com.devhire.entity.User;
import com.devhire.repository.JobRepository;
import com.devhire.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final AiProvider aiProvider;
private final SkillService skillService;

 public JobService(
        JobRepository jobRepository,
        UserRepository userRepository,
        AiProvider aiProvider,
        SkillService skillService
) {
    this.jobRepository = jobRepository;
    this.userRepository = userRepository;
    this.aiProvider = aiProvider;
    this.skillService = skillService;
}

    public JobResponse createJob(
            CreateJobRequest request,
            String userEmail
    ) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        Job job = new Job(
                user,
                request.getTitle().trim(),
                cleanCompanyName(request.getCompanyName()),
                request.getDescription().trim(),
                "CREATED"
        );

        Job savedJob = jobRepository.save(job);
        var extracted =
        aiProvider.extractJobSkills(
                savedJob.getDescription()
        );

skillService.saveJobSkills(
        savedJob,
        extracted.getSkills()
);

savedJob.setStatus("ANALYZED");

jobRepository.save(savedJob);

        return toResponse(savedJob);
    }

    public List<JobResponse> getUserJobs(String userEmail) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        return jobRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public JobResponse getJob(
            Long jobId,
            String userEmail
    ) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        Job job = jobRepository
                .findByIdAndUserId(jobId, user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Job not found")
                );

        return toResponse(job);
    }

    private String cleanCompanyName(String companyName) {

        if (companyName == null ||
                companyName.isBlank()) {

            return null;
        }

        return companyName.trim();
    }

    private JobResponse toResponse(Job job) {

        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getCompanyName(),
                job.getDescription(),
                job.getStatus(),
                job.getCreatedAt()
        );
    }
}