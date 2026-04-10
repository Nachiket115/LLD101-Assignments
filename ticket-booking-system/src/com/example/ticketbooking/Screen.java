package com.example.ticketbooking;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Screen {
    private final String screenId;
    private final List<Seat> seats;
    private final List<Show> shows;

    public Screen(String screenId, List<Seat> seats) {
        this.screenId = Objects.requireNonNull(screenId, "screenId");
        this.seats = List.copyOf(Objects.requireNonNull(seats, "seats"));
        this.shows = new ArrayList<>();
    }

    public String getScreenId() {
        return screenId;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public List<Show> getShows() {
        return List.copyOf(shows);
    }

    public void addShow(Show show) {
        for (Show existingShow : shows) {
            if (existingShow.overlaps(show)) {
                throw new IllegalArgumentException("Show overlaps on screen " + screenId);
            }
        }
        shows.add(show);
    }
}
