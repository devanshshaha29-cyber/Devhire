package com.devhire.service;

import com.devhire.dto.ResumeSummaryResponse;
import com.devhire.dto.ResumeUploadResponse;
import com.devhire.dto.SkillExtractionResult;
import com.devhire.entity.Resume;
import com.devhire.entity.User;
import com.devhire.repository.ResumeRepository;
import com.devhire.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.devhire.dto.ResumeSummaryResponse;
import java.util.List;

import java.io.IOException;

@Service
public class ResumeService {

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeTextExtractorService textExtractorService;
    private final AiProvider aiProvider;
    private final SkillService skillService;

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            ResumeTextExtractorService textExtractorService,
            AiProvider aiProvider,
            SkillService skillService
    ) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.textExtractorService = textExtractorService;
        this.aiProvider = aiProvider;
        this.skillService = skillService;
    }

    public ResumeUploadResponse uploadResume(
            MultipartFile file,
            String userEmail
    ) throws IOException {

        validateFile(file);

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        String extractedText =
                textExtractorService.extractText(file);

        if (extractedText == null ||
                extractedText.isBlank()) {

            throw new IllegalArgumentException(
                    "No readable text was found in the resume"
            );
        }

        Resume resume = new Resume(
                user,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                "ANALYZING"
        );

        Resume savedResume =
                resumeRepository.save(resume);

        SkillExtractionResult aiResult =
                aiProvider.extractResumeSkills(
                        extractedText
                );

        skillService.saveCandidateSkills(
                savedResume,
                aiResult.getSkills()
        );

        savedResume.setStatus("ANALYZED");

        resumeRepository.save(savedResume);

        return new ResumeUploadResponse(
                savedResume.getId(),
                savedResume.getOriginalFileName(),
                savedResume.getContentType(),
                savedResume.getFileSize(),
                savedResume.getStatus(),
                extractedText.length(),
                savedResume.getUploadedAt()
        );
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a resume file"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Resume must be smaller than 5 MB"
            );
        }

        String fileName =
                file.getOriginalFilename();

        if (fileName == null) {
            throw new IllegalArgumentException(
                    "Invalid file name"
            );
        }

        String lowerName =
                fileName.toLowerCase();

        if (!lowerName.endsWith(".pdf")
                && !lowerName.endsWith(".docx")) {

            throw new IllegalArgumentException(
                    "Only PDF and DOCX resumes are supported"
            );
        }
        
    }
    public List<ResumeSummaryResponse> listResumes(
        String userEmail
) {

    User user = userRepository
            .findByEmail(userEmail)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "User not found"
                    )
            );

    return resumeRepository
            .findByUserIdOrderByUploadedAtDesc(
                    user.getId()
            )
            .stream()
            .map(resume ->
                    new ResumeSummaryResponse(
                            resume.getId(),
                            resume.getOriginalFileName(),
                            resume.getStatus(),
                            resume.getUploadedAt()
                    )
            )
            .toList();
}
    
}