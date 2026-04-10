package com.example.snakesladders;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter board dimension n for n x n board: ");
            int dimension = scanner.nextInt();

            System.out.print("Enter number of players: ");
            int playerCount = scanner.nextInt();

            System.out.print("Enter difficulty level (easy/hard): ");
            DifficultyLevel difficultyLevel = DifficultyLevel.from(scanner.next());

            Board board = new BoardGenerator().generate(dimension, difficultyLevel);
            List<Player> players = createPlayers(playerCount);
            Game game = new Game(board, players, new StandardDice(1, 6));

            System.out.println("Board has cells 1 to " + board.getSize());
            System.out.println("Generated " + dimension + " snakes and " + dimension + " ladders.");
            System.out.println("Game starts now.");

            List<Player> winners = game.play();
            System.out.println("Winners in order: " + winners.stream().map(Player::getId).toList());
        }
    }

    private static List<Player> createPlayers(int playerCount) {
        if (playerCount < 2) {
            throw new IllegalArgumentException("At least two players are required.");
        }
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            players.add(new Player("Player-" + i));
        }
        return players;
    }
}
