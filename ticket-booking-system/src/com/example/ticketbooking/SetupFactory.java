package com.example.ticketbooking;

import java.time.Duration;
import java.time.LocalDateTime;

public final class SetupFactory {
    private static final int SCREENS_PER_THEATRE = 2;
    private static final int SHOWS_PER_SCREEN = 3;

    private SetupFactory() {
    }

    public static TicketBookingSystem createSystem(
            int cityCount,
            int theatresPerCity,
            int movieCount,
            int screenCapacity
    ) {
        if (cityCount <= 0 || theatresPerCity <= 0 || movieCount <= 0 || screenCapacity <= 0) {
            throw new IllegalArgumentException("All setup inputs must be positive.");
        }

        TicketBookingSystem system = new TicketBookingSystem(new PricingService(), new PaymentService());
        Movie[] movies = createMovies(system, movieCount);
        LocalDateTime firstShowTime = LocalDateTime.of(2026, 4, 10, 9, 0);

        for (int cityIndex = 1; cityIndex <= cityCount; cityIndex++) {
            City city = new City("C-" + cityIndex, "City-" + cityIndex);
            system.addCity(city);

            for (int theatreIndex = 1; theatreIndex <= theatresPerCity; theatreIndex++) {
                Theatre theatre = new Theatre("T-" + cityIndex + "-" + theatreIndex, "Theatre-" + cityIndex + "-" + theatreIndex);
                city.addTheatre(theatre);

                for (int screenIndex = 1; screenIndex <= SCREENS_PER_THEATRE; screenIndex++) {
                    Screen screen = new Screen(
                            "SCR-" + cityIndex + "-" + theatreIndex + "-" + screenIndex,
                            SeatLayoutFactory.createSeats(screenCapacity)
                    );
                    theatre.addScreen(screen);

                    for (int showIndex = 1; showIndex <= SHOWS_PER_SCREEN; showIndex++) {
                        Movie movie = movies[(showIndex - 1) % movies.length];
                        LocalDateTime startTime = firstShowTime.plusHours((long) (showIndex - 1) * 4);
                        Show show = new Show(
                                "SH-" + cityIndex + "-" + theatreIndex + "-" + screenIndex + "-" + showIndex,
                                movie,
                                city.getCityId(),
                                theatre.getTheatreId(),
                                screen.getScreenId(),
                                startTime,
                                SeatLayoutFactory.createSeats(screenCapacity)
                        );
                        screen.addShow(show);
                        system.addShow(show);
                    }
                }
            }
        }
        return system;
    }

    private static Movie[] createMovies(TicketBookingSystem system, int movieCount) {
        Movie[] movies = new Movie[movieCount];
        for (int i = 1; i <= movieCount; i++) {
            Movie movie = new Movie("M-" + i, "Movie-" + i, Duration.ofMinutes(120), i % 2 == 0 ? "Drama" : "Action");
            movies[i - 1] = movie;
            system.addMovie(movie);
        }
        return movies;
    }
}
