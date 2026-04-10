package com.example.ticketbooking;

import java.time.Duration;
import java.util.Objects;

public final class Movie {
    private final String movieId;
    private final String name;
    private final Duration duration;
    private final String genre;

    public Movie(String movieId, String name, Duration duration, String genre) {
        this.movieId = Objects.requireNonNull(movieId, "movieId");
        this.name = Objects.requireNonNull(name, "name");
        this.duration = Objects.requireNonNull(duration, "duration");
        this.genre = Objects.requireNonNull(genre, "genre");
    }

    public String getMovieId() {
        return movieId;
    }

    public String getName() {
        return name;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getGenre() {
        return genre;
    }

    @Override
    public String toString() {
        return name;
    }
}
