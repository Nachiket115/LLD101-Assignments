package com.example.elevator;

public final class ConsoleElevatorObserver implements ElevatorObserver {
    @Override
    public void onElevatorEvent(String event) {
        System.out.println("[LOG] " + event);
    }
}
