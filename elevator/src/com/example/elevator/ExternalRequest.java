package com.example.elevator;

public final class ExternalRequest extends Request {
    private final int floor;
    private final Direction direction;

    public ExternalRequest(int floor, Direction direction) {
        super("HALL");
        if (direction == Direction.IDLE) {
            throw new IllegalArgumentException("Hall call direction cannot be IDLE.");
        }
        this.floor = floor;
        this.direction = direction;
    }

    public int getFloor() {
        return floor;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public int targetFloor() {
        return floor;
    }

    @Override
    public String toString() {
        return getRequestId() + " external floor=" + floor + " direction=" + direction;
    }
}
