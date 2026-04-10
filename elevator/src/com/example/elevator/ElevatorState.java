package com.example.elevator;

public interface ElevatorState {
    Direction direction();

    void move(Elevator elevator);
}
