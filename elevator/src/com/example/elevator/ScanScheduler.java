package com.example.elevator;

import java.util.Comparator;
import java.util.List;

public final class ScanScheduler implements Scheduler {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request, ElevatorSystemCache cache) {
        return elevators.stream()
                .filter(Elevator::isOperational)
                .filter(Elevator::canAcceptPassenger)
                .min(Comparator
                        .comparingInt((Elevator elevator) -> scanCost(elevator, request))
                        .thenComparingInt(Elevator::pendingStopCount))
                .orElseThrow(() -> new IllegalStateException("No available elevator."));
    }

    private int scanCost(Elevator elevator, Request request) {
        Direction direction = elevator.getDirection();
        int distance = Math.abs(elevator.getCurrentFloor() - request.targetFloor());
        if (direction == Direction.IDLE) {
            return distance;
        }
        if (direction == Direction.UP && request.targetFloor() >= elevator.getCurrentFloor()) {
            return distance;
        }
        if (direction == Direction.DOWN && request.targetFloor() <= elevator.getCurrentFloor()) {
            return distance;
        }
        return distance + 20;
    }
}
