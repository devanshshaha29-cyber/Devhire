package com.devhire.dto;

import jakarta.validation.constraints.NotNull;

public class CreateAnalysisRequest {

    @NotNull
    private Long resumeId;

    @NotNull
    private Long jobId;

    public CreateAnalysisRequest() {
    }

    public Long getResumeId() {
        return resumeId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}