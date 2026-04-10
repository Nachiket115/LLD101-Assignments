package com.example.elevator;

import java.util.Locale;

public enum SystemMode {
    NORMAL,
    HIGH_TRAFFIC,
    EMERGENCY;

    public static SystemMode from(String value) {
        String normalized = value == null ? "" : value.trim().replace('-', '_').toUpperCase(Locale.ROOT);
        for (SystemMode mode : values()) {
            if (mode.name().equals(normalized)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Mode must be normal, high-traffic, or emergency.");
    }
}
