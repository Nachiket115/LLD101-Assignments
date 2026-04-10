package com.example.elevator;

public final class IdleState implements ElevatorState {
    @Override
    public Direction direction() {
        return Direction.IDLE;
    }

    @Override
    public void move(Elevator elevator) {
        elevator.chooseDirectionFromPendingStops();
    }
}
