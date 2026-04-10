package com.example.ticketbooking;

import java.util.List;

public final class Booking {
    private final String bookingId;
    private final User user;
    private final Movie movie;
    private final String theatreId;
    private final String showId;
    private final List<Seat> seats;
    private final double totalPrice;
    private BookingStatus status;

    public Booking(
            String bookingId,
            User user,
            Movie movie,
            String theatreId,
            String showId,
            List<Seat> seats,
            double totalPrice,
            BookingStatus status
    ) {
        this.bookingId = bookingId;
        this.user = user;
        this.movie = movie;
        this.theatreId = theatreId;
        this.showId = showId;
        this.seats = List.copyOf(seats);
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getShowId() {
        return showId;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void markCancelled() {
        status = BookingStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return "Booking{"
                + "bookingId='" + bookingId + '\''
                + ", user=" + user
                + ", movie=" + movie
                + ", showId='" + showId + '\''
                + ", seats=" + seats
                + ", totalPrice=" + totalPrice
                + ", status=" + status
                + '}';
    }
}
