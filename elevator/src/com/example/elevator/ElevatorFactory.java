package com.example.elevator;

import java.util.ArrayList;
import java.util.List;

public final class ElevatorFactory {
    public List<Elevator> createElevators(int count, int capacity) {
        if (count <= 0) {
            throw new IllegalArgumentException("Elevator count must be positive.");
        }
        List<Elevator> elevators = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            elevators.add(new Elevator("E-" + i, capacity));
        }
        return elevators;
    }
}
