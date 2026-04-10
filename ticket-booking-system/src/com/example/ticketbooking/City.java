package com.example.ticketbooking;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class City {
    private final String cityId;
    private final String name;
    private final List<Theatre> theatres;

    public City(String cityId, String name) {
        this.cityId = Objects.requireNonNull(cityId, "cityId");
        this.name = Objects.requireNonNull(name, "name");
        this.theatres = new ArrayList<>();
    }

    public String getCityId() {
        return cityId;
    }

    public String getName() {
        return name;
    }

    public List<Theatre> getTheatres() {
        return List.copyOf(theatres);
    }

    public void addTheatre(Theatre theatre) {
        theatres.add(Objects.requireNonNull(theatre, "theatre"));
    }

    @Override
    public String toString() {
        return name;
    }
}
