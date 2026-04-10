package com.example.elevator;

import java.util.Comparator;
import java.util.List;

public final class LookScheduler implements Scheduler {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request, ElevatorSystemCache cache) {
        return elevators.stream()
                .filter(Elevator::isOperational)
                .filter(Elevator::canAcceptPassenger)
                .min(Comparator
                        .comparingInt((Elevator elevator) -> lookCost(elevator, request, cache))
                        .thenComparingInt(Elevator::pendingStopCount))
                .orElseThrow(() -> new IllegalStateException("No available elevator."));
    }

    private int lookCost(Elevator elevator, Request request, ElevatorSystemCache cache) {
        int distance = Math.abs(elevator.getCurrentFloor() - request.targetFloor());
        int loadPenalty = elevator.getCurrentLoad() * 2;
        int activeFloorBonus = cache.isActiveFloor(request.targetFloor()) ? -2 : 0;
        return distance + elevator.pendingStopCount() + loadPenalty + activeFloorBonus;
    }
}
