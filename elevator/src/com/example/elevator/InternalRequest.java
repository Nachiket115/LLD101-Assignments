package com.example.elevator;

public final class InternalRequest extends Request {
    private final String elevatorId;
    private final int destinationFloor;

    public InternalRequest(String elevatorId, int destinationFloor) {
        super("CABIN");
        this.elevatorId = elevatorId;
        this.destinationFloor = destinationFloor;
    }

    public String getElevatorId() {
        return elevatorId;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    @Override
    public int targetFloor() {
        return destinationFloor;
    }

    @Override
    public String toString() {
        return getRequestId() + " internal elevator=" + elevatorId + " destination=" + destinationFloor;
    }
}
