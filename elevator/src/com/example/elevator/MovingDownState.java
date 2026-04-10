package com.example.elevator;

public final class MovingDownState implements ElevatorState {
    @Override
    public Direction direction() {
        return Direction.DOWN;
    }

    @Override
    public void move(Elevator elevator) {
        elevator.moveDownOneFloor();
    }
}
