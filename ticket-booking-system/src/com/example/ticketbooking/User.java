package com.example.ticketbooking;

import java.util.Objects;

public final class User {
    private final String userId;
    private final String name;

    public User(String userId, String name) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
