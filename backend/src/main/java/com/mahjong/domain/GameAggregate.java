package com.mahjong.domain;

import com.mahjong.domain.model.GameId;
import com.mahjong.domain.model.PlayerId;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class GameAggregate {
    private final GameId id;
    private int version;
    private List<PlayerId> players;
    private final PlayerId createdBy;
    private GameStatus status;

    public enum GameStatus {
        LOBBY, IN_PROGRESS
    }

    public GameAggregate(GameId id, PlayerId createdBy) {
        this.id = id;
        this.createdBy = createdBy;
        this.players = new ArrayList<>();
        this.players.add(createdBy);
        this.status = GameStatus.LOBBY;
        this.version = 0;
    }

    public synchronized void addPlayer(PlayerId newPlayer, int expectedVersion) {
        checkVersion(expectedVersion);
        if (status != GameStatus.LOBBY) {
            throw new IllegalStateException("Can only add players in LOBBY");
        }
        if (players.size() >= 4) {
            throw new IllegalStateException("Game is full");
        }
        if (players.contains(newPlayer)) {
            throw new IllegalStateException("Player already in game");
        }
        players.add(newPlayer);
        this.version++;
    }

    private void checkVersion(int expectedVersion) {
        if (this.version != expectedVersion) {
            throw new ConcurrentModificationException(
                    "Version mismatch: expected " + expectedVersion +
                            ", current " + this.version
            );
        }
    }

    // Геттеры (для тестов)
    public synchronized int getVersion() { return version; }
    public synchronized List<PlayerId> getPlayers() { return List.copyOf(players); }
    public synchronized GameStatus getStatus() { return status; }
    public synchronized GameId getId() { return id; }
}
