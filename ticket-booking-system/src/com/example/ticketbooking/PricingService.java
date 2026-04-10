package com.example.ticketbooking;

import java.time.LocalTime;
import java.util.List;

public final class PricingService {
    public double calculatePrice(Show show, List<Seat> seats, BookingMode mode) {
        double total = 0.0;
        double timeMultiplier = timeMultiplier(show.getStartTime().toLocalTime());
        double modeMultiplier = mode == BookingMode.PREMIUM ? 1.10 : 1.0;

        for (Seat seat : seats) {
            double basePrice = seat.getType() == SeatType.PREMIUM ? 250.0 : 150.0;
            total += basePrice * timeMultiplier * modeMultiplier;
        }
        return total;
    }

    private double timeMultiplier(LocalTime showTime) {
        if (showTime.isBefore(LocalTime.NOON)) {
            return 0.90;
        }
        if (showTime.isBefore(LocalTime.of(18, 0))) {
            return 1.00;
        }
        if (showTime.isBefore(LocalTime.of(22, 0))) {
            return 1.20;
        }
        return 1.10;
    }
}
