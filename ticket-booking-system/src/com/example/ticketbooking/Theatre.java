package com.example.ticketbooking;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Theatre {
    private final String theatreId;
    private final String name;
    private final List<Screen> screens;

    public Theatre(String theatreId, String name) {
        this.theatreId = Objects.requireNonNull(theatreId, "theatreId");
        this.name = Objects.requireNonNull(name, "name");
        this.screens = new ArrayList<>();
    }

    public String getTheatreId() {
        return theatreId;
    }

    public String getName() {
        return name;
    }

    public List<Screen> getScreens() {
        return List.copyOf(screens);
    }

    public void addScreen(Screen screen) {
        screens.add(Objects.requireNonNull(screen, "screen"));
    }

    @Override
    public String toString() {
        return name;
    }
}
