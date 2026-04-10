# Multilevel Parking Lot Design

## 1. Problem Statement
Design a multilevel parking lot system that supports:

- small, medium, and large parking slots
- multiple vehicle types
- multiple entry gates
- nearest compatible slot assignment
- parking tickets
- hourly billing based on allocated slot type
- status reporting for available slots

## 2. Requirement Summary
Slot types:
- `SMALL`: for 2-wheelers
- `MEDIUM`: for cars
- `LARGE`: for buses

Compatibility rules:
- 2-wheeler can park in `SMALL`, `MEDIUM`, or `LARGE`
- car can park in `MEDIUM` or `LARGE`
- bus can park only in `LARGE`

Billing rule:
- billing is based on allocated slot type, not vehicle type
- example: if a bike parks in a medium slot, the medium hourly rate is used

Supported APIs:
- `park(vehicleDetails, entryTime, requestedSlotType, entryGateId)`
- `status()`
- `exit(parkingTicket, exitTime)`

## 3. Class Diagram
The Mermaid version of the UML diagram is available in `UML.md`.

```text
+------------------------------------------------------+
|                  ParkingLotService                   |
+------------------------------------------------------+
| - slots: Map<String, ParkingSlot>                    |
| - gates: Map<String, EntryGate>                      |
| - activeTickets: Map<String, ParkingTicket>          |
| - assignmentStrategy: SlotAssignmentStrategy         |
| - billingService: BillingService                     |
+------------------------------------------------------+
| + park(vehicle, entryTime, requestedType, gateId)    |
| + status(): AvailabilityStatus                       |
| + exit(ticket, exitTime): ParkingBill                |
+------------------------------------------------------+
          | uses                    | uses
          v                         v
+----------------------------+   +----------------------------+
|  SlotAssignmentStrategy    |   |       BillingService       |
+----------------------------+   +----------------------------+
| + assignSlot(...)          |   | - hourlyRates: Map         |
+----------------------------+   +----------------------------+
          ^                       | + generateBill(...)       |
          |                       +----------------------------+
          |
+----------------------------+
| NearestSlotAssignment      |
+----------------------------+
| finds nearest available    |
| compatible slot            |
+----------------------------+

+----------------------------+        +----------------------------+
|        ParkingSlot         |        |         EntryGate          |
+----------------------------+        +----------------------------+
| - slotId: String           |        | - gateId: String           |
| - level: int               |        +----------------------------+
| - type: SlotType           |
| - available: boolean       |
| - distanceByGate: Map      |
+----------------------------+
| + distanceFrom(gateId)     |
| + occupy()                 |
| + release()                |
+----------------------------+

+----------------------------+        +----------------------------+
|       ParkingTicket        |        |        ParkingBill         |
+----------------------------+        +----------------------------+
| - ticketId: String         |        | - ticketId: String         |
| - vehicleDetails           |        | - entryTime                |
| - allocatedSlotId          |        | - exitTime                 |
| - allocatedSlotType        |        | - billedHours              |
| - entryTime                |        | - amount                   |
+----------------------------+        +----------------------------+

+----------------------------+        +----------------------------+
|       VehicleDetails       |        |      AvailabilityStatus    |
+----------------------------+        +----------------------------+
| - registrationNumber       |        | - availableByType: Map     |
| - vehicleType              |        +----------------------------+
| - ownerName                |
+----------------------------+

+----------------------------+        +----------------------------+
|        VehicleType         |        |          SlotType          |
+----------------------------+        +----------------------------+
| TWO_WHEELER                |        | SMALL                      |
| CAR                        |        | MEDIUM                     |
| BUS                        |        | LARGE                      |
+----------------------------+        +----------------------------+
```

## 4. Design Approach
The system uses `ParkingLotService` as the main facade for the required APIs.
Clients do not need to directly manage slots, billing, or ticket storage.

Slot assignment is delegated to `NearestSlotAssignmentStrategy`.
This keeps the assignment logic separate from the main service and makes it easy to add another strategy later.

Billing is delegated to `BillingService`.
This keeps pricing rules separate from parking operations.

## 5. Nearest Slot Assignment
Each `ParkingSlot` stores its distance from each entry gate.
When a vehicle enters:

1. The service validates the entry gate.
2. The assignment strategy filters available slots.
3. It checks slot compatibility using the requested slot type.
4. It picks the compatible slot with the lowest distance from the entry gate.
5. The slot is marked occupied.
6. A ticket is generated.

This lookup is simple and clear. In a production-grade version, free slots can be indexed by gate and type using priority queues for faster assignment.

## 6. Compatibility Rules
The design treats `requestedSlotType` as the minimum slot type needed for the vehicle.

- request `SMALL`: can use `SMALL`, `MEDIUM`, or `LARGE`
- request `MEDIUM`: can use `MEDIUM` or `LARGE`
- request `LARGE`: can use `LARGE`

The service also validates that the requested type is not smaller than what the vehicle requires.
For example, a car cannot request a small slot.

## 7. Billing
Billing is based on the allocated slot type.

For example:
- bike parked in small slot uses small rate
- bike parked in medium slot uses medium rate
- car parked in large slot uses large rate

Parking duration is rounded up to the next hour.
The minimum billing duration is 1 hour.

## 8. Edge Cases Covered
- invalid entry gate
- invalid requested slot type for a vehicle
- no compatible slot available
- duplicate exit for the same ticket
- exit time before entry time
- nearest slot chosen using gate distance
- smaller vehicle assigned to larger slot when needed

## 9. Files
- `App.java` - runnable demo
- `ParkingLotService.java` - main API facade
- `NearestSlotAssignmentStrategy.java` - nearest compatible slot selection
- `SlotAssignmentStrategy.java` - strategy interface
- `ParkingSlot.java` - slot model
- `EntryGate.java` - gate model
- `VehicleDetails.java` - vehicle model
- `VehicleType.java` - vehicle type enum
- `SlotType.java` - slot type enum and compatibility ordering
- `ParkingTicket.java` - generated ticket
- `ParkingBill.java` - generated bill
- `BillingService.java` - billing calculation
- `AvailabilityStatus.java` - status response

## 10. Build And Run
```bash
cd parking-lot/src
javac com/example/parkinglot/*.java
java com.example.parkinglot.App
```

## 11. Sample Output
```text
Initial status: {SMALL=1, MEDIUM=2, LARGE=2}
Ticket{ticketId='T-1', vehicle=KA-01-BK-1001 (TWO_WHEELER), slot='M2', slotType=MEDIUM, entryTime=2026-04-10T09:00}
Ticket{ticketId='T-2', vehicle=KA-01-CA-2020 (CAR), slot='L1', slotType=LARGE, entryTime=2026-04-10T09:10}
After parking: {SMALL=1, MEDIUM=1, LARGE=1}
Bill{ticketId='T-1', slotType=MEDIUM, billedHours=3, amount=180.0}
After bike exit: {SMALL=1, MEDIUM=2, LARGE=1}
```

## 12. Interview Summary
“I designed the parking lot using a service facade, slot assignment strategy, billing service, and domain models for tickets, slots, gates, and vehicles. The assignment strategy chooses the nearest available compatible slot from the entry gate, while billing is calculated from the allocated slot type.”
