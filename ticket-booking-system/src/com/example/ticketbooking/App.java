package com.example.ticketbooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter number of cities: ");
            int cityCount = scanner.nextInt();

            System.out.print("Enter number of theatres per city: ");
            int theatresPerCity = scanner.nextInt();

            System.out.print("Enter number of movies: ");
            int movieCount = scanner.nextInt();

            System.out.print("Enter screen capacity: ");
            int screenCapacity = scanner.nextInt();

            System.out.print("Enter booking mode (normal/premium): ");
            BookingMode bookingMode = BookingMode.from(scanner.next());

            TicketBookingSystem system = SetupFactory.createSystem(cityCount, theatresPerCity, movieCount, screenCapacity);
            System.out.println("Created system with " + cityCount + " cities, " + theatresPerCity
                    + " theatres per city, and " + movieCount + " movies.");

            System.out.println("Cities: " + system.viewCities());

            City selectedCity = system.viewCities().get(0);
            System.out.println("Theatres in " + selectedCity.getName() + ": " + system.viewTheatres(selectedCity.getCityId()));
            System.out.println("Movies in " + selectedCity.getName() + ": " + system.viewMoviesInCity(selectedCity.getCityId()));

            Movie selectedMovie = system.viewMoviesInCity(selectedCity.getCityId()).get(0);
            List<Show> shows = system.viewShows(selectedCity.getCityId(), selectedMovie.getMovieId());
            Show selectedShow = shows.get(0);
            System.out.println("Available shows for " + selectedMovie.getName() + ": " + shows.size());

            List<String> selectedSeats = List.of("A1", "A2");
            System.out.println("Selected seats: " + selectedSeats);

            User user = new User("U-1", "User-1");
            Booking booking = system.book(user, selectedShow.getShowId(), selectedSeats, bookingMode);
            System.out.println("Booking confirmed: " + booking);
            System.out.println("Show runs: " + selectedShow.hasBooking());

            Booking cancelled = system.cancel(booking.getBookingId(), LocalDateTime.of(2026, 4, 10, 8, 30));
            System.out.println("Cancelled booking: " + cancelled.getStatus());
        }
    }
}
