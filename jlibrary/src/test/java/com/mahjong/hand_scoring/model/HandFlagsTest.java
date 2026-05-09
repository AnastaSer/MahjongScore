package com.mahjong.hand_scoring.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class HandFlagsTest {
    private Optional<HandFlags> createFrom(List<HandFlags.Flag> flags) {
        try {
            return Optional.of(new HandFlags(flags));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<HandFlags> createFrom(HandFlags.Flag flag) {
        try {
            return Optional.of(new HandFlags(flag));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Test
    public void incorrectTest() {
        incorrectList(List.of(
                List.of(HandFlags.Flag.CLEAR_SUIT, HandFlags.Flag.CLEAR_SUIT_WITH_TRUMPS),
                List.of(HandFlags.Flag.CLEAR_SUIT, HandFlags.Flag.TRUMPS),
                List.of(HandFlags.Flag.CLEAR_SUIT_WITH_TRUMPS, HandFlags.Flag.TRUMPS),
                List.of(HandFlags.Flag.TRUMPS, HandFlags.Flag.TRUMPS_ONES_NINES),
                List.of(HandFlags.Flag.CLEAR_SUIT, HandFlags.Flag.CLEAR_SUIT_WITH_TRUMPS, HandFlags.Flag.TRUMPS),
                List.of(HandFlags.Flag.CLEAR_SUIT, HandFlags.Flag.CLEAR_SUIT_WITH_TRUMPS, HandFlags.Flag.TRUMPS_ONES_NINES),
                List.of(HandFlags.Flag.CLEAR_SUIT, HandFlags.Flag.TRUMPS, HandFlags.Flag.TRUMPS_ONES_NINES),
                List.of(HandFlags.Flag.CLEAR_SUIT_WITH_TRUMPS, HandFlags.Flag.TRUMPS, HandFlags.Flag.TRUMPS_ONES_NINES),
                List.of(HandFlags.Flag.CLEAR_SUIT, HandFlags.Flag.CLEAR_SUIT_WITH_TRUMPS, HandFlags.Flag.TRUMPS, HandFlags.Flag.TRUMPS_ONES_NINES)
                ));
        incorrectList(List.of(
                List.of(HandFlags.Flag.MIZER),
                List.of(HandFlags.Flag.NO_ORDEREDS),
                List.of(HandFlags.Flag.HAS_ALL_DRAGONS),
                List.of(HandFlags.Flag.HAS_ALL_WINDS),
                List.of(HandFlags.Flag.WAS_WAITING_FROM_THE_START),
                List.of(HandFlags.Flag.FINISHED_WITH_ONE_POSSIBLE),
                List.of(HandFlags.Flag.FINISHED_FROM_THE_WALL),
                List.of(HandFlags.Flag.FINISHED_WITH_FREE_TILE),
                List.of(HandFlags.Flag.FINISHED_WITH_LAST_IN_GAME),
                List.of(HandFlags.Flag.FINISHED_BY_ROBBING_OPEN_KONG)
        ));
        incorrectList(List.of(
                List.of(HandFlags.Flag.MAHJONG, HandFlags.Flag.HAS_ALL_WINDS, HandFlags.Flag.HAS_ALL_DRAGONS),
                List.of(HandFlags.Flag.MAHJONG, HandFlags.Flag.MIZER, HandFlags.Flag.HAS_ALL_DRAGONS),
                List.of(HandFlags.Flag.MAHJONG, HandFlags.Flag.MIZER, HandFlags.Flag.HAS_ALL_WINDS),
                List.of(HandFlags.Flag.MAHJONG, HandFlags.Flag.MIZER, HandFlags.Flag.NO_ORDEREDS),
                List.of(HandFlags.Flag.MAHJONG, HandFlags.Flag.MIZER, HandFlags.Flag.TRUMPS),
                List.of(HandFlags.Flag.MAHJONG, HandFlags.Flag.MIZER, HandFlags.Flag.TRUMPS_ONES_NINES)
        ));
    }

    private void incorrectList(List<List<HandFlags.Flag>> incorrectFlags) {
        incorrectFlags.stream().forEach(flagList ->  {
            System.out.println("Тестирую: " + flagList);
            assert(createFrom(flagList).isEmpty());
        });
    }

    @Test
    public void correctTest() {}

    @Test
    public void incorrectMethods() {}

    @Test
    public void correctMethods() {}
}
