package com.example.ticketbooking;

import java.util.ArrayList;
import java.util.List;

public final class SeatLayoutFactory {
    private SeatLayoutFactory() {
    }

    public static List<Seat> createSeats(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Screen capacity must be positive.");
        }

        List<Seat> seats = new ArrayList<>();
        int columns = Math.min(10, capacity);
        int rows = (int) Math.ceil(capacity / (double) columns);
        int created = 0;

        for (int row = 1; row <= rows && created < capacity; row++) {
            for (int column = 1; column <= columns && created < capacity; column++) {
                String seatId = rowName(row) + column;
                SeatType seatType = row > rows / 2 ? SeatType.PREMIUM : SeatType.REGULAR;
                seats.add(new Seat(seatId, row, column, seatType));
                created++;
            }
        }
        return seats;
    }

    private static String rowName(int row) {
        return String.valueOf((char) ('A' + row - 1));
    }
}
