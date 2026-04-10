package com.example.ticketbooking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TicketBookingSystem {
    private final Map<String, City> cities = new LinkedHashMap<>();
    private final Map<String, Movie> movies = new LinkedHashMap<>();
    private final Map<String, Show> shows = new LinkedHashMap<>();
    private final Map<String, Booking> bookings = new HashMap<>();
    private final PricingService pricingService;
    private final PaymentService paymentService;
    private int bookingSequence = 1;

    public TicketBookingSystem(PricingService pricingService, PaymentService paymentService) {
        this.pricingService = pricingService;
        this.paymentService = paymentService;
    }

    public void addCity(City city) {
        cities.put(city.getCityId(), city);
    }

    public void addMovie(Movie movie) {
        movies.put(movie.getMovieId(), movie);
    }

    public void addShow(Show show) {
        shows.put(show.getShowId(), show);
    }

    public List<City> viewCities() {
        return List.copyOf(cities.values());
    }

    public List<Theatre> viewTheatres(String cityId) {
        City city = requireCity(cityId);
        return city.getTheatres();
    }

    public List<Movie> viewMoviesInCity(String cityId) {
        requireCity(cityId);
        Map<String, Movie> runningMovies = new LinkedHashMap<>();
        for (Show show : shows.values()) {
            if (show.getCityId().equals(cityId)) {
                runningMovies.put(show.getMovie().getMovieId(), show.getMovie());
            }
        }
        return List.copyOf(runningMovies.values());
    }

    public List<Show> viewShows(String cityId, String movieId) {
        requireCity(cityId);
        if (!movies.containsKey(movieId)) {
            throw new IllegalArgumentException("Invalid movie: " + movieId);
        }
        List<Show> availableShows = new ArrayList<>();
        for (Show show : shows.values()) {
            if (show.getCityId().equals(cityId) && show.getMovie().getMovieId().equals(movieId)) {
                availableShows.add(show);
            }
        }
        return availableShows;
    }

    public Booking book(User user, String showId, List<String> seatIds, BookingMode mode) {
        Show show = requireShow(showId);
        LocalDateTime now = LocalDateTime.now();

        List<Seat> selectedSeats = show.lockSeats(seatIds, user.getUserId(), now);
        double totalPrice = pricingService.calculatePrice(show, selectedSeats, mode);
        PaymentStatus paymentStatus = paymentService.pay(user.getUserId(), totalPrice, mode);

        if (paymentStatus == PaymentStatus.SUCCESS) {
            List<Seat> bookedSeats = show.confirmSeats(seatIds, user.getUserId());
            Booking booking = new Booking(
                    "B-" + bookingSequence++,
                    user,
                    show.getMovie(),
                    show.getTheatreId(),
                    show.getShowId(),
                    bookedSeats,
                    totalPrice,
                    BookingStatus.CONFIRMED
            );
            bookings.put(booking.getBookingId(), booking);
            return booking;
        }

        show.releaseSeats(seatIds, user.getUserId());
        Booking booking = new Booking(
                "B-" + bookingSequence++,
                user,
                show.getMovie(),
                show.getTheatreId(),
                show.getShowId(),
                List.of(),
                totalPrice,
                paymentStatus == PaymentStatus.PENDING ? BookingStatus.PENDING : BookingStatus.FAILED
        );
        bookings.put(booking.getBookingId(), booking);
        return booking;
    }

    public Booking cancel(String bookingId, LocalDateTime cancelTime) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Invalid booking: " + bookingId);
        }
        Show show = requireShow(booking.getShowId());
        if (!cancelTime.isBefore(show.getStartTime())) {
            throw new IllegalStateException("Booking cannot be cancelled after show start time.");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled.");
        }

        show.cancelBookedSeats(booking.getSeats());
        paymentService.refund(booking.getBookingId(), booking.getTotalPrice());
        booking.markCancelled();
        return booking;
    }

    public Show requireShow(String showId) {
        Show show = shows.get(showId);
        if (show == null) {
            throw new IllegalArgumentException("Invalid show: " + showId);
        }
        return show;
    }

    private City requireCity(String cityId) {
        City city = cities.get(cityId);
        if (city == null) {
            throw new IllegalArgumentException("Invalid city: " + cityId);
        }
        return city;
    }

}
