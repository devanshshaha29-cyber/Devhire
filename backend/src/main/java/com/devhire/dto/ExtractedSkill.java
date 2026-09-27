package com.devhire.dto;

public class ExtractedSkill {

    private String name;
    private String category;
    private double confidence;
    private String requirementLevel;

    public ExtractedSkill() {
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getRequirementLevel() {
        return requirementLevel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public void setRequirementLevel(String requirementLevel) {
        this.requirementLevel = requirementLevel;
    }
}