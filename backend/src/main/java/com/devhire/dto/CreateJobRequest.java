package com.devhire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateJobRequest {

    @NotBlank(message = "Job title is required")
    @Size(max = 200)
    private String title;

    @Size(max = 200)
    private String companyName;

    @NotBlank(message = "Job description is required")
    @Size(
            min = 50,
            max = 20000,
            message = "Job description must contain between 50 and 20,000 characters"
    )
    private String description;

    public CreateJobRequest() {
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}