package com.devhire.dto;

import java.time.LocalDateTime;

public class JobResponse {

    private Long id;
    private String title;
    private String companyName;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    public JobResponse(
            Long id,
            String title,
            String companyName,
            String description,
            String status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.title = title;
        this.companyName = companyName;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}