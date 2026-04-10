# Elevator System Design

## 1. Problem Statement
Design an elevator system that can handle multiple elevators, external hall calls, internal cabin calls, scheduling strategies, caching, fault handling, concurrency, and special operating modes.

The application takes input:
- `n`: number of floors
- `e`: number of elevators
- `capacity`: max people per elevator
- `mode`: `normal`, `high-traffic`, or `emergency`

Floors are numbered from `0` to `n`.

## 2. Functional Coverage
- Building has `e` elevators.
- Each elevator has ID, current floor, direction, capacity, load, pending stops, and state.
- External requests are made from a floor with `UP` or `DOWN`.
- Internal requests are made inside an elevator with a destination floor.
- Elevators move one floor at a time.
- Elevators continue in the same direction while there are pending stops in that direction.
- Elevators change direction only when no stops remain in the current direction.
- Idle elevators can pick the nearest suitable request.
- Emergency mode sends all operational elevators to floor `0`.
- High traffic mode uses a scheduler that considers active/frequently requested floors.
- Elevator failure drains pending stops and reassigns them to other operational elevators.

## 3. Class Diagram
The standalone Mermaid UML diagram is available in [`uml.md`](uml.md).

## 4. Text Architecture Diagram
```text
User / App
   |
   v
+---------------------------+
| ElevatorController        |  Singleton facade
+---------------------------+
   |       |        |      |
   |       |        |      +----------------+
   |       |        |                       |
   v       v        v                       v
Dispatch  Cache    FaultHandler       Observer Logger
Service   Layer    reassigns stops    logs events
   |
   v
Scheduler Strategy
   |
   +--> NearestCarScheduler
   +--> ScanScheduler
   +--> LookScheduler
   |
   v
Elevator objects
   |
   v
State Pattern
   |
   +--> IdleState
   +--> MovingUpState
   +--> MovingDownState
```

## 5. Core Classes

### `ElevatorController`
Central singleton controller. It exposes the main operations:
- configure the system
- submit external requests
- submit internal requests
- step the simulation
- activate emergency mode
- handle elevator failures
- print status

### `Elevator`
Represents one elevator. It stores:
- elevator ID
- current floor
- capacity
- current load
- operational/failure status
- pending stops
- current movement state

### `Request`
Abstract base type for requests. Two concrete request types are used:
- `ExternalRequest`: floor + direction
- `InternalRequest`: elevator ID + destination floor

### `DispatchService`
Handles assignment of requests to elevators. It uses the selected scheduling strategy and updates the cache.

### `Scheduler`
Strategy interface for elevator assignment. Implementations:
- `NearestCarScheduler`
- `ScanScheduler`
- `LookScheduler`

### `ElevatorSystemCache`
Thread-safe cache layer for:
- elevator positions
- pending requests
- frequently requested floors

### `FaultHandler`
Handles elevator failure by:
- marking elevator as failed
- draining its pending stops
- reassigning those stops to healthy elevators
- exposing a retry helper for service/network-like failures

## 6. Design Pattern Mapping
```text
Singleton  -> ElevatorController
Strategy   -> Scheduler, NearestCarScheduler, ScanScheduler, LookScheduler
Observer   -> ElevatorObserver, ConsoleElevatorObserver
Factory    -> ElevatorFactory, SchedulerFactory
State      -> ElevatorState, IdleState, MovingUpState, MovingDownState
```

## 7. Cache Design
The cache layer is represented by `ElevatorSystemCache`.

It keeps:
- `elevatorPositions`: latest known floor for each elevator
- `pendingRequests`: recently seen requests
- `floorFrequency`: count of requests by floor

Eviction policies:
- `LruCache`: removes least recently used entry when full
- `LfuCache`: removes least frequently used entry when full

Use cases:
- Quickly inspect elevator positions
- Identify the most active floor during high traffic
- Let LOOK scheduling reduce cost for active floors

## 8. Request Flow
```text
External hall call
   |
   v
ElevatorController.submitExternalRequest(...)
   |
   v
DispatchService.dispatch(...)
   |
   v
Scheduler.selectElevator(...)
   |
   v
Elevator.addStop(...)
   |
   v
Observer logs assignment
```

Internal cabin calls use the same controller but are routed directly to the selected elevator:

```text
Internal cabin call
   |
   v
ElevatorController.submitInternalRequest(elevatorId, destinationFloor)
   |
   v
Elevator.addStop(destinationFloor)
   |
   v
Cache records frequently requested destination
```

## 9. Movement Rules
Each simulation step moves an elevator by one floor:
- `IdleState` chooses a direction if stops exist.
- `MovingUpState` moves one floor up.
- `MovingDownState` moves one floor down.
- The elevator removes a stop when it reaches that floor.
- It continues in the same direction if more stops exist in that direction.
- It changes direction only when the current direction has no pending stops.

## 10. Special Modes

### Normal Mode
Uses `ScanScheduler`. This follows the elevator direction and avoids unnecessary direction changes.

### High Traffic Mode
Uses `LookScheduler`. It considers distance, current load, future stops, and frequently requested floors from cache.

### Emergency Mode
Ignores normal external requests and sends all operational elevators to ground floor `0`.

## 11. Fault Tolerance
When an elevator fails:
- it is marked non-operational
- its pending stops are drained
- pending stops are converted into new requests
- healthy elevators receive reassigned requests

The `FaultHandler.retry(...)` method models retrying a failed operation.

## 12. Concurrency And Consistency
- Controller operations are synchronized.
- Elevator mutation methods are synchronized.
- Caches use synchronized methods or `ConcurrentHashMap`.
- Request assignment marks requests as assigned.
- Failed elevator pending stops are drained atomically from the elevator.
- Capacity is checked before passenger boarding.

## 13. Build And Run
```bash
cd elevator/src
javac com/example/elevator/*.java
java com.example.elevator.App
```

## 14. Sample Input
```text
12
3
6
high-traffic
```

## 15. Sample Output
```text
Enter number of floors: Enter number of elevators: Enter elevator capacity: Enter mode (normal/high-traffic/emergency): [LOG] Controller configured with 3 elevators and mode HIGH_TRAFFIC
[LOG] Assigned external request at floor 3 to E-1
[LOG] Added cabin destination 7 to E-1
[LOG] Assigned external request at floor 9 to E-2
...
E-1{floor=7, direction=IDLE, load=0/6, stops=[]}
E-2{floor=8, direction=UP, load=1/6, stops=[9]}
E-3{floor=0, direction=IDLE, load=0/6, stops=[]}
```

## 16. Interview Summary
“I designed the elevator system around a singleton controller, a dispatch service, pluggable scheduler strategies, a cache layer with LRU/LFU eviction, synchronized elevator state updates, observer-based logging, and a fault handler that reassigns pending stops when an elevator fails.”
