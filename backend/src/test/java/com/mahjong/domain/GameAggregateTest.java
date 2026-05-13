package com.mahjong.domain;

import com.mahjong.domain.model.GameId;
import com.mahjong.domain.model.PlayerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.List;
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

    @Test
    void concurrentAddPlayer_OnlyOneSucceeds() throws Exception {
        // Дано: игра с Алисой (создатель) и Бобом (уже добавлен)
        PlayerId charlie = new PlayerId("Charlie");
        game.addPlayer(bob, 0);
        int versionBeforeConcurrent = game.getVersion(); // Должна быть 1

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        // Два параллельных вызова addPlayer для Чарли с одной и той же ожидаемой версией
        Callable<Void> task = () -> {
            game.addPlayer(charlie, versionBeforeConcurrent);
            return null;
        };

        List<Future<Void>> futures = executor.invokeAll(List.of(task, task));
        // Сколько успешных вызовов?
        long successCount = futures.stream()
                .filter(f -> {
                    try {
                        f.get();
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        assertEquals(1, successCount, "Ровно один вызов должен быть успешным");
        assertTrue(game.getPlayers().contains(charlie), "Чарли должен быть в игре");

        executor.shutdown();
    }
}
