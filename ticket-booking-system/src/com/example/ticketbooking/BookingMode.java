package com.example.ticketbooking;

import java.util.Locale;

public enum BookingMode {
    NORMAL,
    PREMIUM;

    public static BookingMode from(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        for (BookingMode mode : values()) {
            if (mode.name().equals(normalized)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Booking mode must be normal or premium.");
    }
}
