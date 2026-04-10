package com.example.snakesladders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Game {
    private final Board board;
    private final List<Player> activePlayers;
    private final List<Player> winners;
    private final Dice dice;
    private int currentTurnIndex;

    public Game(Board board, List<Player> players, Dice dice) {
        this.board = Objects.requireNonNull(board, "board");
        this.activePlayers = new ArrayList<>(Objects.requireNonNull(players, "players"));
        this.winners = new ArrayList<>();
        this.dice = Objects.requireNonNull(dice, "dice");
        validatePlayers(this.activePlayers);
        this.currentTurnIndex = 0;
    }

    public List<Player> play() {
        while (activePlayers.size() >= 2) {
            TurnResult result = playTurn();
            System.out.println(result);
        }
        if (!activePlayers.isEmpty()) {
            System.out.println(activePlayers.get(0).getId() + " is the last remaining player.");
        }
        return List.copyOf(winners);
    }

    public TurnResult playTurn() {
        if (activePlayers.size() < 2) {
            throw new IllegalStateException("Game already has fewer than two active players.");
        }

        Player player = activePlayers.get(currentTurnIndex);
        DiceRoll roll = dice.roll();
        int startPosition = player.getPosition();
        int attemptedPosition = startPosition + roll.getTotal();
        boolean overshot = attemptedPosition > board.getSize();

        int landingPosition = overshot ? startPosition : attemptedPosition;
        int resolvedPosition = overshot ? startPosition : board.resolvePosition(landingPosition);
        player.moveTo(resolvedPosition);

        boolean won = board.isFinalCell(player.getPosition());
        if (won) {
            player.markFinished();
            winners.add(player);
            activePlayers.remove(currentTurnIndex);
        }

        if (!activePlayers.isEmpty()) {
            if (!won) {
                currentTurnIndex++;
            }
            currentTurnIndex = currentTurnIndex % activePlayers.size();
        }

        return new TurnResult(player, roll, startPosition, player.getPosition(), overshot, won, false, "");
    }

    private void validatePlayers(List<Player> players) {
        if (players.size() < 2) {
            throw new IllegalArgumentException("At least two players are required.");
        }
    }
}
