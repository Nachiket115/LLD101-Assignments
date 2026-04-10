package com.example.elevator;

public final class MovingUpState implements ElevatorState {
    @Override
    public Direction direction() {
        return Direction.UP;
    }

    @Override
    public void move(Elevator elevator) {
        elevator.moveUpOneFloor();
    }
}
