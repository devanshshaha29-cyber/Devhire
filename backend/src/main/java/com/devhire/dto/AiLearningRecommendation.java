package com.devhire.dto;

public class AiLearningRecommendation {

    private String skill;
    private String whyNeeded;
    private String whatToLearn;
    private String learningSequence;
    private String suggestedProject;
    private String difficulty;

    public AiLearningRecommendation() {
    }

    public String getSkill() {
        return skill;
    }

    public String getWhyNeeded() {
        return whyNeeded;
    }

    public String getWhatToLearn() {
        return whatToLearn;
    }

    public String getLearningSequence() {
        return learningSequence;
    }

    public String getSuggestedProject() {
        return suggestedProject;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setWhyNeeded(String whyNeeded) {
        this.whyNeeded = whyNeeded;
    }

    public void setWhatToLearn(String whatToLearn) {
        this.whatToLearn = whatToLearn;
    }

    public void setLearningSequence(String learningSequence) {
        this.learningSequence = learningSequence;
    }

    public void setSuggestedProject(String suggestedProject) {
        this.suggestedProject = suggestedProject;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}