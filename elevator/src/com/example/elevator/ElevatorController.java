package com.example.elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ElevatorController {
    private static final ElevatorController INSTANCE = new ElevatorController();

    private final List<ElevatorObserver> observers = new ArrayList<>();
    private int maxFloor;
    private SystemMode mode;
    private List<Elevator> elevators;
    private ElevatorSystemCache cache;
    private DispatchService dispatchService;
    private FaultHandler faultHandler;

    private ElevatorController() {
    }

    public static ElevatorController getInstance() {
        return INSTANCE;
    }

    public synchronized void configure(
            int floors,
            int elevatorCount,
            int capacity,
            SystemMode mode,
            EvictionPolicy evictionPolicy,
            int cacheSize
    ) {
        if (floors <= 0) {
            throw new IllegalArgumentException("Number of floors must be positive.");
        }
        this.maxFloor = floors;
        this.mode = Objects.requireNonNull(mode, "mode");
        this.elevators = new ElevatorFactory().createElevators(elevatorCount, capacity);
        this.cache = new ElevatorSystemCache(cacheSize, evictionPolicy);
        this.dispatchService = new DispatchService(SchedulerFactory.forMode(mode), cache);
        this.faultHandler = new FaultHandler(dispatchService);
        notifyObservers("Controller configured with " + elevatorCount + " elevators and mode " + mode);
    }

    public synchronized void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }

    public synchronized Elevator submitExternalRequest(int floor, Direction direction) {
        validateFloor(floor);
        if (mode == SystemMode.EMERGENCY) {
            throw new IllegalStateException("Normal requests are ignored in emergency mode.");
        }
        Elevator elevator = dispatchService.dispatch(elevators, new ExternalRequest(floor, direction));
        notifyObservers("Assigned external request at floor " + floor + " to " + elevator.getElevatorId());
        return elevator;
    }

    public synchronized void submitInternalRequest(String elevatorId, int destinationFloor) {
        validateFloor(destinationFloor);
        Elevator elevator = findElevator(elevatorId);
        if (!elevator.canAcceptPassenger()) {
            throw new IllegalStateException("Elevator is at capacity.");
        }
        elevator.addStop(destinationFloor);
        cache.markFloorRequested(destinationFloor);
        notifyObservers("Added cabin destination " + destinationFloor + " to " + elevatorId);
    }

    public synchronized void stepSimulation() {
        if (mode == SystemMode.EMERGENCY) {
            for (Elevator elevator : elevators) {
                elevator.addStop(0);
            }
        }
        for (Elevator elevator : elevators) {
            int before = elevator.getCurrentFloor();
            elevator.step();
            cache.updateElevatorPosition(elevator.getElevatorId(), elevator.getCurrentFloor());
            if (before != elevator.getCurrentFloor()) {
                notifyObservers(elevator.getElevatorId() + " moved from " + before + " to " + elevator.getCurrentFloor());
            }
        }
    }

    public synchronized void failElevator(String elevatorId) {
        Elevator elevator = findElevator(elevatorId);
        faultHandler.handleElevatorFailure(elevator, elevators);
        notifyObservers("Failure handled for " + elevatorId + "; pending stops reassigned.");
    }

    public synchronized void activateEmergencyMode() {
        mode = SystemMode.EMERGENCY;
        for (Elevator elevator : elevators) {
            if (elevator.isOperational()) {
                elevator.addStop(0);
            }
        }
        notifyObservers("Emergency mode activated. Elevators are moving to ground floor.");
    }

    public synchronized void printStatus() {
        for (Elevator elevator : elevators) {
            System.out.println(elevator);
        }
        System.out.println("Cache positions: " + cache.elevatorPositions());
        System.out.println("Most active floor: " + cache.mostActiveFloor().orElse(-1));
    }

    private Elevator findElevator(String elevatorId) {
        for (Elevator elevator : elevators) {
            if (elevator.getElevatorId().equals(elevatorId)) {
                return elevator;
            }
        }
        throw new IllegalArgumentException("Unknown elevator: " + elevatorId);
    }

    private void validateFloor(int floor) {
        if (floor < 0 || floor > maxFloor) {
            throw new IllegalArgumentException("Floor must be between 0 and " + maxFloor);
        }
    }

    private void notifyObservers(String event) {
        for (ElevatorObserver observer : observers) {
            observer.onElevatorEvent(event);
        }
    }
}
