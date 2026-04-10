package com.example.elevator;

import java.util.Comparator;
import java.util.List;

public final class NearestCarScheduler implements Scheduler {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request, ElevatorSystemCache cache) {
        return elevators.stream()
                .filter(Elevator::isOperational)
                .filter(Elevator::canAcceptPassenger)
                .min(Comparator
                        .comparingInt((Elevator elevator) -> Math.abs(elevator.getCurrentFloor() - request.targetFloor()))
                        .thenComparingInt(Elevator::pendingStopCount)
                        .thenComparingInt(Elevator::getCurrentLoad))
                .orElseThrow(() -> new IllegalStateException("No available elevator."));
    }
}
