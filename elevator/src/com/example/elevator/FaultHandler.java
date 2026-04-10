package com.example.elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;

public final class FaultHandler {
    private final DispatchService dispatchService;

    public FaultHandler(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    public void handleElevatorFailure(Elevator failedElevator, List<Elevator> allElevators) {
        failedElevator.markFailed();
        NavigableSet<Integer> pendingStops = failedElevator.drainPendingStops();
        List<Elevator> healthyElevators = new ArrayList<>();
        for (Elevator elevator : allElevators) {
            if (elevator.isOperational()) {
                healthyElevators.add(elevator);
            }
        }
        for (Integer floor : pendingStops) {
            dispatchService.dispatch(healthyElevators, new ExternalRequest(floor, Direction.UP));
        }
    }

    public void retry(String operationName, Runnable operation, int attempts) {
        RuntimeException lastFailure = null;
        for (int i = 0; i < attempts; i++) {
            try {
                operation.run();
                return;
            } catch (RuntimeException exception) {
                lastFailure = exception;
            }
        }
        throw new IllegalStateException("Operation failed after retries: " + operationName, lastFailure);
    }
}
