package com.example.elevator;

import java.util.List;

public interface Scheduler {
    Elevator selectElevator(List<Elevator> elevators, Request request, ElevatorSystemCache cache);
}
