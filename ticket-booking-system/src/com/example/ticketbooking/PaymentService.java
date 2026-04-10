package com.example.ticketbooking;

public final class PaymentService {
    public PaymentStatus pay(String userId, double amount, BookingMode mode) {
        if (amount <= 0) {
            return PaymentStatus.FAILED;
        }
        return mode == BookingMode.PREMIUM ? PaymentStatus.SUCCESS : PaymentStatus.SUCCESS;
    }

    public PaymentStatus refund(String bookingId, double amount) {
        return amount > 0 ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}
