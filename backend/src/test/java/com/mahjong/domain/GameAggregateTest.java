package com.mahjong.domain;

import com.mahjong.domain.model.GameId;
import com.mahjong.domain.model.PlayerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameAggregateTest {
    private GameAggregate game;
    private PlayerId alice;
    private PlayerId bob;

    @BeforeEach
    void setUp() {
        alice = new PlayerId("Alice");
        bob = new PlayerId("Bob");
        game = new GameAggregate(new GameId("GAME-1"), alice);
    }

    @Test
    void versionIncrementsOnAddPlayer() {
        assertEquals(0, game.getVersion());
        game.addPlayer(bob, 0);
        assertEquals(1, game.getVersion());
    }

    @Test
    void wrongVersionThrowsException() {
        game.addPlayer(bob, 0);  // version becomes 1
        assertThrows(ConcurrentModificationException.class,
                () -> game.addPlayer(new PlayerId("Charlie"), 0));
    }
}
