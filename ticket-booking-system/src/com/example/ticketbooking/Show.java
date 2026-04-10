package com.example.ticketbooking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Show {
    private static final int LOCK_MINUTES = 5;

    private final String showId;
    private final Movie movie;
    private final String cityId;
    private final String theatreId;
    private final String screenId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Map<String, Seat> seats;
    private boolean hasBooking;

    public Show(
            String showId,
            Movie movie,
            String cityId,
            String theatreId,
            String screenId,
            LocalDateTime startTime,
            List<Seat> seats
    ) {
        this.showId = Objects.requireNonNull(showId, "showId");
        this.movie = Objects.requireNonNull(movie, "movie");
        this.cityId = Objects.requireNonNull(cityId, "cityId");
        this.theatreId = Objects.requireNonNull(theatreId, "theatreId");
        this.screenId = Objects.requireNonNull(screenId, "screenId");
        this.startTime = Objects.requireNonNull(startTime, "startTime");
        this.endTime = startTime.plus(movie.getDuration());
        this.seats = new HashMap<>();
        for (Seat seat : seats) {
            this.seats.put(seat.getSeatId(), seat);
        }
    }

    public String getShowId() {
        return showId;
    }

    public Movie getMovie() {
        return movie;
    }

    public String getCityId() {
        return cityId;
    }

    public String getTheatreId() {
        return theatreId;
    }

    public String getScreenId() {
        return screenId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public synchronized List<Seat> lockSeats(List<String> seatIds, String userId, LocalDateTime now) {
        expireLocks(now);
        List<Seat> selectedSeats = findSeats(seatIds);
        for (Seat seat : selectedSeats) {
            if (!seat.isAvailable(now)) {
                throw new IllegalStateException("Seat is not available: " + seat.getSeatId());
            }
        }
        for (Seat seat : selectedSeats) {
            seat.lock(userId, now.plusMinutes(LOCK_MINUTES));
        }
        return List.copyOf(selectedSeats);
    }

    public synchronized List<Seat> confirmSeats(List<String> seatIds, String userId) {
        List<Seat> selectedSeats = findSeats(seatIds);
        for (Seat seat : selectedSeats) {
            if (seat.getStatus() != SeatStatus.LOCKED || !userId.equals(seat.getLockedBy())) {
                throw new IllegalStateException("Seat is not locked by user: " + seat.getSeatId());
            }
        }
        for (Seat seat : selectedSeats) {
            seat.book();
        }
        hasBooking = true;
        return List.copyOf(selectedSeats);
    }

    public synchronized void releaseSeats(List<String> seatIds, String userId) {
        for (Seat seat : findSeats(seatIds)) {
            if (seat.getStatus() == SeatStatus.LOCKED && userId.equals(seat.getLockedBy())) {
                seat.release();
            }
        }
    }

    public synchronized void cancelBookedSeats(List<Seat> seatsToCancel) {
        for (Seat seatToCancel : seatsToCancel) {
            Seat seat = seats.get(seatToCancel.getSeatId());
            if (seat != null && seat.getStatus() == SeatStatus.BOOKED) {
                seat.release();
            }
        }
    }

    public synchronized List<Seat> availableSeats(LocalDateTime now) {
        expireLocks(now);
        List<Seat> availableSeats = new ArrayList<>();
        for (Seat seat : seats.values()) {
            if (seat.getStatus() == SeatStatus.AVAILABLE) {
                availableSeats.add(seat);
            }
        }
        return availableSeats;
    }

    public synchronized boolean hasBooking() {
        return hasBooking;
    }

    public boolean overlaps(Show other) {
        return startTime.isBefore(other.endTime) && other.startTime.isBefore(endTime);
    }

    private List<Seat> findSeats(List<String> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("At least one seat is required.");
        }
        List<Seat> selectedSeats = new ArrayList<>();
        for (String seatId : seatIds) {
            Seat seat = seats.get(seatId);
            if (seat == null) {
                throw new IllegalArgumentException("Invalid seat: " + seatId);
            }
            selectedSeats.add(seat);
        }
        return selectedSeats;
    }

    private void expireLocks(LocalDateTime now) {
        for (Seat seat : seats.values()) {
            if (seat.isExpiredLock(now)) {
                seat.release();
            }
        }
    }

    @Override
    public String toString() {
        return showId + " | " + movie.getName() + " | " + startTime.toLocalTime() + "-" + endTime.toLocalTime();
    }
}
