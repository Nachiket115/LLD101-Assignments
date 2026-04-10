package com.example.elevator;

import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

public final class Elevator {
    private final String elevatorId;
    private final int capacity;
    private final NavigableSet<Integer> pendingStops;
    private int currentFloor;
    private int currentLoad;
    private boolean operational;
    private ElevatorState state;

    public Elevator(String elevatorId, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Elevator capacity must be positive.");
        }
        this.elevatorId = Objects.requireNonNull(elevatorId, "elevatorId");
        this.capacity = capacity;
        this.pendingStops = new TreeSet<>();
        this.currentFloor = 0;
        this.currentLoad = 0;
        this.operational = true;
        this.state = new IdleState();
    }

    public synchronized String getElevatorId() {
        return elevatorId;
    }

    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    public synchronized Direction getDirection() {
        return state.direction();
    }

    public synchronized int getCapacity() {
        return capacity;
    }

    public synchronized int getCurrentLoad() {
        return currentLoad;
    }

    public synchronized boolean isOperational() {
        return operational;
    }

    public synchronized int pendingStopCount() {
        return pendingStops.size();
    }

    public synchronized boolean canAcceptPassenger() {
        return currentLoad < capacity;
    }

    public synchronized void boardPassenger() {
        if (currentLoad >= capacity) {
            throw new IllegalStateException("Elevator cannot exceed capacity.");
        }
        currentLoad++;
    }

    public synchronized void exitPassenger() {
        if (currentLoad > 0) {
            currentLoad--;
        }
    }

    public synchronized void addStop(int floor) {
        if (!operational) {
            throw new IllegalStateException("Elevator is not operational.");
        }
        pendingStops.add(floor);
        chooseDirectionFromPendingStops();
    }

    public synchronized void step() {
        if (!operational) {
            return;
        }
        state.move(this);
        if (pendingStops.remove(currentFloor)) {
            exitPassenger();
        }
        chooseDirectionFromPendingStops();
    }

    public synchronized NavigableSet<Integer> drainPendingStops() {
        NavigableSet<Integer> copy = new TreeSet<>(pendingStops);
        pendingStops.clear();
        state = new IdleState();
        return copy;
    }

    public synchronized void markFailed() {
        operational = false;
        state = new IdleState();
    }

    public synchronized void markRecovered() {
        operational = true;
    }

    synchronized void moveUpOneFloor() {
        currentFloor++;
    }

    synchronized void moveDownOneFloor() {
        currentFloor--;
    }

    synchronized void chooseDirectionFromPendingStops() {
        if (pendingStops.isEmpty()) {
            state = new IdleState();
            return;
        }
        boolean hasAbove = pendingStops.higher(currentFloor) != null;
        boolean hasBelow = pendingStops.lower(currentFloor) != null;
        if (state.direction() == Direction.UP && hasAbove) {
            return;
        }
        if (state.direction() == Direction.DOWN && hasBelow) {
            return;
        }
        if (hasAbove) {
            state = new MovingUpState();
        } else if (hasBelow) {
            state = new MovingDownState();
        } else {
            pendingStops.remove(currentFloor);
            state = new IdleState();
        }
    }

    @Override
    public synchronized String toString() {
        return elevatorId + "{floor=" + currentFloor + ", direction=" + getDirection()
                + ", load=" + currentLoad + "/" + capacity + ", stops=" + pendingStops + "}";
    }
}
