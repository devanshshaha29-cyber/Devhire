package com.devhire.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SkillNormalizationService {

    private static final Map<String, String> ALIASES =
            Map.ofEntries(
                    Map.entry("js", "javascript"),
                    Map.entry("ecmascript", "javascript"),
                    Map.entry("javascript", "javascript"),

                    Map.entry("spring", "spring"),
                    Map.entry("spring framework", "spring"),
                    Map.entry("spring boot", "spring boot"),

                    Map.entry("postgres", "postgresql"),
                    Map.entry("postgresql", "postgresql"),

                    Map.entry("node", "node.js"),
                    Map.entry("nodejs", "node.js"),
                    Map.entry("node.js", "node.js"),

                    Map.entry("reactjs", "react"),
                    Map.entry("react.js", "react"),
                    Map.entry("react", "react")
            );

    public String normalize(String skillName) {

        if (skillName == null) {
            return "";
        }

        String cleaned =
                skillName
                        .trim()
                        .toLowerCase();

        return ALIASES.getOrDefault(
                cleaned,
                cleaned
        );
    }
}