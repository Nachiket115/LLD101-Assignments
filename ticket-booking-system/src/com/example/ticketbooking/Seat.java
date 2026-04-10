package com.example.ticketbooking;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Seat {
    private final String seatId;
    private final int row;
    private final int column;
    private final SeatType type;
    private SeatStatus status;
    private String lockedBy;
    private LocalDateTime lockedUntil;

    public Seat(String seatId, int row, int column, SeatType type) {
        this.seatId = Objects.requireNonNull(seatId, "seatId");
        this.type = Objects.requireNonNull(type, "type");
        this.row = row;
        this.column = column;
        this.status = SeatStatus.AVAILABLE;
    }

    public String getSeatId() {
        return seatId;
    }

    public SeatType getType() {
        return type;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public boolean isAvailable(LocalDateTime now) {
        return status == SeatStatus.AVAILABLE || isExpiredLock(now);
    }

    public boolean isExpiredLock(LocalDateTime now) {
        return status == SeatStatus.LOCKED && lockedUntil != null && !lockedUntil.isAfter(now);
    }

    public void lock(String userId, LocalDateTime lockedUntil) {
        this.status = SeatStatus.LOCKED;
        this.lockedBy = Objects.requireNonNull(userId, "userId");
        this.lockedUntil = Objects.requireNonNull(lockedUntil, "lockedUntil");
    }

    public void book() {
        this.status = SeatStatus.BOOKED;
        this.lockedBy = null;
        this.lockedUntil = null;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.lockedBy = null;
        this.lockedUntil = null;
    }

    @Override
    public String toString() {
        return seatId;
    }
}
