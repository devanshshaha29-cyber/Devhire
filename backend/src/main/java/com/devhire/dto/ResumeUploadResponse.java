package com.devhire.dto;

import java.time.LocalDateTime;

public class ResumeUploadResponse {

    private Long id;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private String status;
    private int extractedCharacterCount;
    private LocalDateTime uploadedAt;

    public ResumeUploadResponse(
            Long id,
            String originalFileName,
            String contentType,
            Long fileSize,
            String status,
            int extractedCharacterCount,
            LocalDateTime uploadedAt
    ) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.status = status;
        this.extractedCharacterCount = extractedCharacterCount;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getStatus() {
        return status;
    }

    public int getExtractedCharacterCount() {
        return extractedCharacterCount;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}