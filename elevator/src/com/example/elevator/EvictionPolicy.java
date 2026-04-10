package com.example.elevator;

import java.util.Locale;

public enum EvictionPolicy {
    LRU,
    LFU;

    public static EvictionPolicy from(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        for (EvictionPolicy policy : values()) {
            if (policy.name().equals(normalized)) {
                return policy;
            }
        }
        return LRU;
    }
}
