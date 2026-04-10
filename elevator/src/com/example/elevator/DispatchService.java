package com.example.elevator;

import java.util.List;

public final class DispatchService {
    private final Scheduler scheduler;
    private final ElevatorSystemCache cache;

    public DispatchService(Scheduler scheduler, ElevatorSystemCache cache) {
        this.scheduler = scheduler;
        this.cache = cache;
    }

    public synchronized Elevator dispatch(List<Elevator> elevators, Request request) {
        cache.addPendingRequest(request);
        Elevator elevator = scheduler.selectElevator(elevators, request, cache);
        elevator.addStop(request.targetFloor());
        if (request instanceof ExternalRequest) {
            elevator.boardPassenger();
        }
        request.markAssigned();
        return elevator;
    }
}
