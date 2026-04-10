package com.example.snakesladders;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Board {
    private final int size;
    private final Map<Integer, Jump> jumps;

    public Board(int size, List<Snake> snakes, List<Ladder> ladders) {
        if (size < 10) {
            throw new IllegalArgumentException("Board size must be at least 10.");
        }
        this.size = size;
        this.jumps = new HashMap<>();
        addSnakes(snakes);
        addLadders(ladders);
    }

    public int getSize() {
        return size;
    }

    public Map<Integer, Jump> getJumps() {
        return Map.copyOf(jumps);
    }

    public boolean isFinalCell(int cell) {
        return cell == size;
    }

    public int resolvePosition(int position) {
        int current = position;
        while (jumps.containsKey(current)) {
            current = jumps.get(current).getEnd();
        }
        return current;
    }

    private void addSnakes(List<Snake> snakes) {
        for (Snake snake : snakes) {
            validateJump(snake, "snake");
            jumps.put(snake.getStart(), snake);
        }
    }

    private void addLadders(List<Ladder> ladders) {
        for (Ladder ladder : ladders) {
            validateJump(ladder, "ladder");
            jumps.put(ladder.getStart(), ladder);
        }
    }

    private void validateJump(Jump jump, String type) {
        if (jump == null) {
            throw new IllegalArgumentException("Jump cannot be null.");
        }
        if (!jump.isValidFor(size)) {
            throw new IllegalArgumentException("Invalid " + type + ": " + jump.getStart() + " -> " + jump.getEnd());
        }
        if (jumps.containsKey(jump.getStart())) {
            throw new IllegalArgumentException("Overlapping jump at cell " + jump.getStart());
        }
        validateNoCycle(jump);
    }

    private void validateNoCycle(Jump newJump) {
        Map<Integer, Jump> candidateJumps = new HashMap<>(jumps);
        candidateJumps.put(newJump.getStart(), newJump);

        for (Integer start : candidateJumps.keySet()) {
            Set<Integer> visited = new HashSet<>();
            int current = start;
            while (candidateJumps.containsKey(current)) {
                if (!visited.add(current)) {
                    throw new IllegalArgumentException("Snakes and ladders cannot create a cycle.");
                }
                current = candidateJumps.get(current).getEnd();
            }
        }
    }
}
