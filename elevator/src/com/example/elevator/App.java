package com.example.elevator;

import java.util.Scanner;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter number of floors: ");
            int floors = scanner.nextInt();

            System.out.print("Enter number of elevators: ");
            int elevatorCount = scanner.nextInt();

            System.out.print("Enter elevator capacity: ");
            int capacity = scanner.nextInt();

            System.out.print("Enter mode (normal/high-traffic/emergency): ");
            SystemMode mode = SystemMode.from(scanner.next());

            ElevatorController controller = ElevatorController.getInstance();
            controller.addObserver(new ConsoleElevatorObserver());
            controller.configure(floors, elevatorCount, capacity, mode, EvictionPolicy.LRU, 20);

            if (mode == SystemMode.EMERGENCY) {
                controller.activateEmergencyMode();
            } else {
                Elevator assigned = controller.submitExternalRequest(3, Direction.UP);
                controller.submitInternalRequest(assigned.getElevatorId(), Math.min(floors, 7));
                controller.submitExternalRequest(Math.min(floors, 9), Direction.DOWN);
            }

            for (int i = 0; i < 8; i++) {
                controller.stepSimulation();
            }

            controller.printStatus();
        }
    }
}
