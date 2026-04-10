package com.example.snakesladders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class BoardGenerator {
    private final Random random;

    public BoardGenerator() {
        this(new Random());
    }

    public BoardGenerator(Random random) {
        this.random = random;
    }

    public Board generate(int dimension, DifficultyLevel difficultyLevel) {
        if (dimension < 5) {
            throw new IllegalArgumentException("Board dimension must be at least 5 to place snakes and ladders safely.");
        }

        int boardSize = dimension * dimension;
        int jumpCount = dimension;
        List<Snake> snakes = new ArrayList<>();
        List<Ladder> ladders = new ArrayList<>();
        Set<Integer> usedCells = new HashSet<>();

        while (snakes.size() < jumpCount) {
            Snake snake = randomSnake(boardSize, difficultyLevel);
            if (reserveCells(snake, usedCells)) {
                snakes.add(snake);
            }
        }

        while (ladders.size() < jumpCount) {
            Ladder ladder = randomLadder(boardSize, difficultyLevel);
            if (reserveCells(ladder, usedCells)) {
                ladders.add(ladder);
            }
        }

        return new Board(boardSize, snakes, ladders);
    }

    private Snake randomSnake(int boardSize, DifficultyLevel difficultyLevel) {
        int minimumDrop = difficultyLevel == DifficultyLevel.HARD ? 5 : 2;
        int head = randomCell(3 + minimumDrop, boardSize - 1);
        int tail = randomCell(2, head - minimumDrop);
        return new Snake(head, tail);
    }

    private Ladder randomLadder(int boardSize, DifficultyLevel difficultyLevel) {
        int minimumClimb = difficultyLevel == DifficultyLevel.EASY ? 5 : 2;
        int start = randomCell(2, boardSize - minimumClimb - 1);
        int end = randomCell(start + minimumClimb, boardSize - 1);
        return new Ladder(start, end);
    }

    private int randomCell(int minInclusive, int maxInclusive) {
        return random.nextInt(maxInclusive - minInclusive + 1) + minInclusive;
    }

    private boolean reserveCells(Jump jump, Set<Integer> usedCells) {
        if (usedCells.contains(jump.getStart()) || usedCells.contains(jump.getEnd())) {
            return false;
        }
        usedCells.add(jump.getStart());
        usedCells.add(jump.getEnd());
        return true;
    }
}
