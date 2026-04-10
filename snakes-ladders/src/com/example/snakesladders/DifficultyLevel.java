package com.example.snakesladders;

import java.util.Locale;

public enum DifficultyLevel {
    EASY,
    HARD;

    public static DifficultyLevel from(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        for (DifficultyLevel difficultyLevel : values()) {
            if (difficultyLevel.name().equals(normalized)) {
                return difficultyLevel;
            }
        }
        throw new IllegalArgumentException("Difficulty must be easy or hard.");
    }
}
