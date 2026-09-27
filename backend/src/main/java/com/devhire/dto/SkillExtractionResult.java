package com.devhire.dto;

import java.util.ArrayList;
import java.util.List;

public class SkillExtractionResult {

    private List<ExtractedSkill> skills = new ArrayList<>();

    public SkillExtractionResult() {
    }

    public List<ExtractedSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<ExtractedSkill> skills) {
        this.skills = skills;
    }
}