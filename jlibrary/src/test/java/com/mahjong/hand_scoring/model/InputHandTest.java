package com.mahjong.hand_scoring.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mahjong.hand_scoring.model.HandFlags.Flag.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class InputHandTest {
    private static final Rules activeRules = RulesSet.load();
    private static final Wind west = Wind.WEST;
    private static final Wind east = Wind.EAST;

    private static final Combination closedOneDot9 = Combination.of("closed single dot 9");
    private static final Combination openedThreeDot9 = Combination.of("opened three dot 9");
    private static final Combination closedOrderedThreeDot3 = Combination.of("closed ordered three dot 3");
    private static final Combination closedOrderedThreeDot7 = Combination.of("closed ordered three dot 7");

    private static final Combination closedOneSign1 = Combination.of("closed single sign 1");
    private static final Combination closedOneSign2 = Combination.of("closed single sign 2");
    private static final Combination closedOneSign3 = Combination.of("closed single sign 3");
    private static final Combination closedOneSign4 = Combination.of("closed single sign 4");
    private static final Combination closedPairSign3 = Combination.of("closed pair sign 3");
    private static final Combination closedPairSign7 = Combination.of("closed pair sign 7");
    private static final Combination openedThreeSign1 = Combination.of("opened three sign 1");
    private static final Combination openedThreeSign3 = Combination.of("opened three sign 3");
    private static final Combination openedThreeSign5 = Combination.of("opened three sign 5");
    private static final Combination openedThreeSign9 = Combination.of("opened three sign 9");
    private static final Combination closedThreeSign3 = Combination.of("closed three sign 3");
    private static final Combination closedFourSign3 = Combination.of("closed four sign 3");
    private static final Combination openedOrderedThreeSign1 = Combination.of("opened ordered three sign 1");
    private static final Combination closedOrderedThreeSign5 = Combination.of("closed ordered three sign 5");
    private static final Combination closedOrderedFourSign1 = Combination.of("closed ordered four sign 1");

    private static final Combination bonus = Combination.of("opened single bonus flower 1");

    private static final Combination closedDragon1 = Combination.of("closed single dragon 1");
    private static final Combination openedPairDragon1 = Combination.of("opened pair dragon 1");
    private static final Combination openedThreeDragon1 = Combination.of("opened three dragon 1");
    private static final Combination openedThreeDragon2 = Combination.of("opened three dragon 2");
    private static final Combination openedThreeDragon3 = Combination.of("opened three dragon 3");

    private static final Combination closedWind2 = Combination.of("closed single wind 2");
    private static final Combination openedPairWind1 = Combination.of("opened pair wind 1");
    private static final Combination openedPairWind3 = Combination.of("opened pair wind 3");
    private static final Combination closedPairWind2 = Combination.of("closed pair wind 2");
    private static final Combination closedPairWind4 = Combination.of("closed pair wind 4");
    private static final Combination openedThreeWind1 = Combination.of("opened three wind 1");
    private static final Combination openedThreeWind2 = Combination.of("opened three wind 2");
    private static final Combination openedThreeWind3 = Combination.of("opened three wind 3");
    private static final Combination openedThreeWind4 = Combination.of("opened three wind 4");

    private Optional<InputHand> createFrom(List<Combination> combinations, Optional<HandFlags> inputFlags) {
        try {
            if (inputFlags.isPresent())
                return Optional.of(new InputHand(west, east, inputFlags.get(), combinations));
            else
                return Optional.of(new InputHand(west, east, combinations));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<InputHand> createFrom(Optional<HandFlags> inputFlags, List<InputTile> inputTiles) {
        try {
            if (inputFlags.isPresent())
                return Optional.of(new InputHand(inputTiles, west, east, inputFlags.get(), activeRules));
            else
                return Optional.of(new InputHand(inputTiles, west, east, activeRules));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Test
    public void correctTests() {
        new InputHand(west, east, null, new ArrayList<>());
        new InputHand(west, east, new HandFlags(), null);
        new InputHand(null, west, east, new HandFlags(), activeRules);
        new InputHand(new ArrayList<>(), west, east, null, activeRules);

        assert(createFrom(List.of(openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1),
                Optional.empty()).isPresent());
        assert(createFrom(List.of(openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1,
                        closedDragon1),
                Optional.empty()).isPresent());
        assert(createFrom(List.of(openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1,
                        closedDragon1, bonus),
                Optional.empty()).isPresent());
    }

    @Test
    public void incorrectTests() {
        System.out.println("Тестирую playerWind null ");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new InputHand(null, east, new HandFlags(), new ArrayList<>()))
                .withMessage("Задайте ветер игрока");

        System.out.println("Тестирую vipWind null ");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new InputHand( east, null, new HandFlags(), new ArrayList<>()))
                .withMessage("Задайте преимущественный ветер");

        assert(createFrom(List.of(openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1, closedOneSign1),
                Optional.empty()).isEmpty());
        assert(createFrom(List.of(openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1, closedOneSign2),
                Optional.empty()).isEmpty());
        assert(createFrom(List.of(openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1, closedOneSign3),
                Optional.empty()).isEmpty());

        assert(createFrom(List.of(openedThreeSign3, closedThreeSign3),
                Optional.empty()).isEmpty());
        assert(createFrom(List.of(openedThreeSign3, openedThreeSign3),
                Optional.empty()).isEmpty());
        assert(createFrom(List.of(closedThreeSign3, closedThreeSign3),
                Optional.empty()).isEmpty());

        assert(createFrom(List.of(closedFourSign3, closedFourSign3),
                Optional.empty()).isEmpty());
        assert(createFrom(List.of(closedPairSign3, closedPairSign3),
                Optional.empty()).isEmpty());

        assert(createFrom(List.of(closedPairSign3, closedPairSign3),
                Optional.empty()).isEmpty());

        assert(createFrom(List.of(openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1,
                        closedDragon1, closedWind2, closedOneSign4),
                Optional.empty()).isEmpty());

        assert(createFrom(List.of(openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1, openedOrderedThreeSign1,
                        closedDragon1, closedWind2),
                Optional.empty()).isEmpty());
    }

    @Test
    public void testInputFlags() {
        HandFlags clearSuit = new HandFlags(CLEAR_SUIT);
        // negative
        assert (createFrom(List.of(openedOrderedThreeSign1, closedDragon1),
                Optional.of(clearSuit)).isEmpty());
        assert (createFrom(List.of(openedOrderedThreeSign1, closedWind2),
                Optional.of(clearSuit)).isEmpty());
        assert (createFrom(List.of(openedOrderedThreeSign1, closedOneDot9),
                Optional.of(clearSuit)).isEmpty());
        assert (createFrom(List.of(closedWind2, closedDragon1),
                Optional.of(clearSuit)).isEmpty());
        // positive
        assert (createFrom(List.of(openedOrderedThreeSign1, closedOneSign2),
                Optional.of(clearSuit)).isPresent());
        assert (createFrom(List.of(openedOrderedThreeSign1, closedOneSign3, bonus),
                Optional.of(clearSuit)).isPresent());
        assert (createFrom(List.of(openedOrderedThreeSign1),
                Optional.of(clearSuit)).isPresent());

        HandFlags clearSuitWithTrumps = new HandFlags(CLEAR_SUIT_WITH_TRUMPS);
        // negative
        assert (createFrom(List.of(openedOrderedThreeSign1, closedOneDot9, closedDragon1),
                Optional.of(clearSuitWithTrumps)).isEmpty());
        assert (createFrom(List.of(openedOrderedThreeSign1, closedOneDot9, closedWind2),
                Optional.of(clearSuitWithTrumps)).isEmpty());
        assert (createFrom(List.of(openedOrderedThreeSign1, closedOneDot9, closedDragon1, closedWind2),
                Optional.of(clearSuitWithTrumps)).isEmpty());
        assert (createFrom(List.of(openedOrderedThreeSign1, closedOneDot9),
                Optional.of(clearSuitWithTrumps)).isEmpty());
        // positive
        assert (createFrom(List.of(openedOrderedThreeSign1, closedDragon1),
                Optional.of(clearSuitWithTrumps)).isPresent());
        assert (createFrom(List.of(openedOrderedThreeSign1, closedWind2),
                Optional.of(clearSuitWithTrumps)).isPresent());
        assert (createFrom(List.of(openedOrderedThreeSign1, closedDragon1, closedWind2),
                Optional.of(clearSuitWithTrumps)).isPresent());
        assert (createFrom(List.of(openedOrderedThreeSign1),
                Optional.of(clearSuitWithTrumps)).isPresent());
        assert (createFrom(List.of(openedOrderedThreeSign1, closedDragon1, bonus),
                Optional.of(clearSuitWithTrumps)).isPresent());
        assert (createFrom(List.of(openedOrderedThreeSign1, closedWind2, bonus),
                Optional.of(clearSuitWithTrumps)).isPresent());
        assert (createFrom(List.of(openedOrderedThreeSign1, closedDragon1, closedWind2, bonus),
                Optional.of(clearSuitWithTrumps)).isPresent());
        assert (createFrom(List.of(openedOrderedThreeSign1, bonus),
                Optional.of(clearSuitWithTrumps)).isPresent());

        HandFlags trumps = new HandFlags(TRUMPS);
        // negative
        assert (createFrom(List.of(closedOneDot9, closedDragon1),
                Optional.of(trumps)).isEmpty());
        assert (createFrom(List.of(closedOneDot9, closedWind2),
                Optional.of(trumps)).isEmpty());
        assert (createFrom(List.of(closedOneDot9, closedDragon1, closedWind2),
                Optional.of(trumps)).isEmpty());
        // positive
        assert (createFrom(List.of(closedDragon1),
                Optional.of(trumps)).isPresent());
        assert (createFrom(List.of(closedWind2),
                Optional.of(trumps)).isPresent());
        assert (createFrom(List.of(closedDragon1, closedWind2),
                Optional.of(trumps)).isPresent());
        assert (createFrom(List.of(closedDragon1, bonus),
                Optional.of(trumps)).isPresent());
        assert (createFrom(List.of(closedWind2, bonus),
                Optional.of(trumps)).isPresent());
        assert (createFrom(List.of(closedDragon1, closedWind2, bonus),
                Optional.of(trumps)).isPresent());
        assert (createFrom(List.of(bonus),
                Optional.of(trumps)).isPresent());

        HandFlags trumps19 = new HandFlags(TRUMPS_ONES_NINES);
        // negative
        assert (createFrom(List.of(closedDragon1, closedOneSign3),
                Optional.of(trumps19)).isEmpty());
        // positive
        assert (createFrom(List.of(closedDragon1),
                Optional.of(trumps19)).isPresent());
        assert (createFrom(List.of(closedWind2),
                Optional.of(trumps19)).isPresent());
        assert (createFrom(List.of(closedDragon1, closedWind2),
                Optional.of(trumps19)).isPresent());
        assert (createFrom(List.of(closedDragon1, bonus),
                Optional.of(trumps19)).isPresent());
        assert (createFrom(List.of(closedWind2, bonus),
                Optional.of(trumps19)).isPresent());
        assert (createFrom(List.of(closedDragon1, closedWind2, bonus),
                Optional.of(trumps19)).isPresent());
        assert (createFrom(List.of(bonus),
                Optional.of(trumps19)).isPresent());
        assert (createFrom(List.of(closedOneSign1),
                Optional.of(trumps19)).isPresent());
        assert (createFrom(List.of(closedOneDot9),
                Optional.of(trumps19)).isPresent());
        assert (createFrom(List.of(closedOneSign1, closedOneDot9),
                Optional.of(trumps19)).isPresent());
        assert (createFrom(List.of(closedOneSign1, closedOneDot9, closedWind2),
                Optional.of(trumps19)).isPresent());

        HandFlags mahjongMizer = new HandFlags(MAHJONG, MIZER);
        // negative
        assert (createFrom(List.of(openedPairWind1),
                Optional.of(mahjongMizer)).isEmpty());
        assert (createFrom(List.of(openedPairWind3),
                Optional.of(mahjongMizer)).isEmpty());
        assert (createFrom(List.of(openedPairDragon1),
                Optional.of(mahjongMizer)).isEmpty());
        assert (createFrom(List.of(openedThreeWind2),
                Optional.of(mahjongMizer)).isEmpty());
        assert (createFrom(List.of(openedThreeDragon2),
                Optional.of(mahjongMizer)).isEmpty());
        assert (createFrom(List.of(closedThreeSign3),
                Optional.of(mahjongMizer)).isEmpty());
        assert (createFrom(List.of(closedFourSign3),
                Optional.of(mahjongMizer)).isEmpty());
        // positive
        assert (createFrom(new ArrayList<>(),
                Optional.of(mahjongMizer)).isPresent());
        assert (createFrom(List.of(openedOrderedThreeSign1),
                Optional.of(mahjongMizer)).isPresent());
        assert (createFrom(List.of(closedOrderedFourSign1),
                Optional.of(mahjongMizer)).isPresent());
        assert (createFrom(List.of(closedOneDot9),
                Optional.of(mahjongMizer)).isPresent());
        assert (createFrom(List.of(closedDragon1, closedWind2),
                Optional.of(mahjongMizer)).isPresent());
        assert (createFrom(List.of(bonus),
                Optional.of(mahjongMizer)).isPresent());

        HandFlags mahjongNoOrdereds = new HandFlags(MAHJONG, NO_ORDEREDS);
        // negative
        assert (createFrom(List.of(openedOrderedThreeSign1),
                Optional.of(mahjongNoOrdereds)).isEmpty());
        assert (createFrom(List.of(closedOrderedFourSign1),
                Optional.of(mahjongNoOrdereds)).isEmpty());
        assert (createFrom(List.of(openedThreeDragon2, openedThreeWind2, openedThreeDot9),
                Optional.of(mahjongNoOrdereds)).isEmpty());
        // positive
        assert (createFrom(List.of(openedThreeDragon2, openedThreeWind2, openedThreeDot9, closedFourSign3, bonus),
                Optional.of(mahjongNoOrdereds)).isPresent());
        assert (createFrom(List.of(openedThreeDragon2, openedThreeWind2, openedThreeDot9, closedFourSign3),
                Optional.of(mahjongNoOrdereds)).isPresent());
        assert (createFrom(List.of(openedThreeDragon2, openedThreeWind2, openedThreeDot9, closedFourSign3, openedPairWind1, bonus),
                Optional.of(mahjongNoOrdereds)).isPresent());
        assert (createFrom(List.of(openedThreeDragon2, openedThreeWind2, openedThreeDot9, closedFourSign3, openedPairWind1),
                Optional.of(mahjongNoOrdereds)).isPresent());

        HandFlags mahjongAllDragons = new HandFlags(MAHJONG, HAS_ALL_DRAGONS);
        // negative
        assert (createFrom(List.of(openedThreeDragon1, openedThreeDragon2, openedThreeDragon3),
                Optional.of(mahjongAllDragons)).isEmpty());
        assert (createFrom(List.of(openedPairDragon1, openedThreeDragon2),
                Optional.of(mahjongAllDragons)).isEmpty());
        // positive
        assert (createFrom(List.of(openedPairDragon1, openedThreeDragon2, openedThreeDragon3),
                Optional.of(mahjongAllDragons)).isPresent());

        HandFlags mahjongAllWinds = new HandFlags(MAHJONG, HAS_ALL_WINDS);
        // negative
        assert (createFrom(List.of(openedThreeWind1, openedThreeWind2, openedThreeWind3, openedThreeWind4),
                Optional.of(mahjongAllWinds)).isEmpty());
        assert (createFrom(List.of(openedPairWind1, openedThreeWind2, openedThreeWind3),
                Optional.of(mahjongAllWinds)).isEmpty());
        // positive
        assert (createFrom(List.of(openedPairWind1, openedThreeWind2, openedThreeWind3, openedThreeWind4),
                Optional.of(mahjongAllWinds)).isPresent());

        HandFlags mahjongNoOrderedsAndDragons = new HandFlags(MAHJONG,
                HAS_ALL_DRAGONS, NO_ORDEREDS);
        // negative
        assert(createFrom(List.of(openedPairDragon1, openedThreeDragon2, openedThreeDragon3),
                Optional.of(mahjongNoOrderedsAndDragons)).isEmpty());
        // positive
        assert(createFrom(List.of(openedPairDragon1, openedThreeDragon2, openedThreeDragon3, openedThreeWind4, openedThreeDot9),
                Optional.of(mahjongNoOrderedsAndDragons)).isPresent());

        HandFlags mahjongNoOrderedsAndWinds = new HandFlags(MAHJONG,
                HAS_ALL_WINDS, NO_ORDEREDS);
        // negative
        assert(createFrom(List.of(openedPairWind1, openedThreeWind2, openedThreeWind3, openedThreeWind4),
                Optional.of(mahjongNoOrderedsAndWinds)).isEmpty());
        // positive
        assert(createFrom(List.of(openedPairWind1, openedThreeWind2, openedThreeWind3, openedThreeWind4, openedThreeDot9),
                Optional.of(mahjongNoOrderedsAndWinds)).isPresent());
    }

    @Test
    public void testSetFlags() {
        testOneSet(List.of(MAHJONG, HAS_ALL_WINDS, NO_ORDEREDS, TRUMPS_ONES_NINES, CLEAR_SUIT_WITH_TRUMPS),
                List.of(openedPairWind1, openedThreeWind2, openedThreeWind3, openedThreeWind4, openedThreeDot9));
        testOneSet(List.of(MAHJONG, HAS_ALL_WINDS, NO_ORDEREDS, TRUMPS),
                List.of(openedPairWind1, openedThreeWind2, openedThreeWind3, openedThreeWind4, openedThreeDragon1));
        testOneSet(List.of(MAHJONG, HAS_ALL_WINDS, CLEAR_SUIT_WITH_TRUMPS),
                List.of(openedPairWind1, openedThreeWind2, openedThreeWind3, openedThreeWind4, openedOrderedThreeSign1));

        testOneSet(List.of(MAHJONG, HAS_ALL_DRAGONS, NO_ORDEREDS, TRUMPS),
                List.of(openedPairDragon1, openedThreeDragon2, openedThreeDragon3, openedThreeWind4, openedThreeWind1));
        testOneSet(List.of(MAHJONG, HAS_ALL_DRAGONS, NO_ORDEREDS, TRUMPS_ONES_NINES, CLEAR_SUIT_WITH_TRUMPS),
                List.of(openedPairDragon1, openedThreeDragon2, openedThreeDragon3, openedThreeWind4, openedThreeDot9));
        testOneSet(List.of(MAHJONG, HAS_ALL_DRAGONS, CLEAR_SUIT_WITH_TRUMPS),
                List.of(openedPairDragon1, openedThreeDragon2, openedThreeDragon3, openedOrderedThreeSign1, openedThreeSign3));
        testOneSet(List.of(MAHJONG, HAS_ALL_DRAGONS),
                List.of(openedPairDragon1, openedThreeDragon2, openedThreeDragon3, openedOrderedThreeSign1, openedThreeDot9));

        testOneSet(List.of(MAHJONG, MIZER),
                List.of(closedPairSign3, closedOrderedFourSign1, openedOrderedThreeSign1, closedOrderedThreeDot3, closedOrderedThreeDot7));
        testOneSet(List.of(MAHJONG, MIZER, CLEAR_SUIT),
                List.of(closedPairSign3, closedOrderedFourSign1, openedOrderedThreeSign1, closedOrderedThreeSign5, closedOrderedThreeSign5));

        testOneSet(List.of(MAHJONG, MIZER),
                List.of(closedPairWind2, closedOrderedFourSign1, openedOrderedThreeSign1, closedOrderedThreeDot3, closedOrderedThreeDot7));
        testOneSet(List.of(MAHJONG, MIZER, CLEAR_SUIT_WITH_TRUMPS),
                List.of(closedPairWind2, closedOrderedFourSign1, openedOrderedThreeSign1, closedOrderedThreeSign5, closedOrderedThreeSign5));

        testOneSet(List.of(MAHJONG),
                List.of(openedPairWind1, closedOrderedFourSign1, openedOrderedThreeSign1, closedOrderedThreeDot3, closedOrderedThreeDot7));
        testOneSet(List.of(MAHJONG, CLEAR_SUIT_WITH_TRUMPS),
                List.of(openedPairWind3, closedOrderedFourSign1, openedOrderedThreeSign1, closedOrderedThreeSign5, closedOrderedThreeSign5));

        testOneSet(List.of(MAHJONG),
                List.of(openedPairDragon1, closedOrderedFourSign1, openedOrderedThreeSign1, closedOrderedThreeDot3, closedOrderedThreeDot7));
        testOneSet(List.of(MAHJONG, CLEAR_SUIT_WITH_TRUMPS),
                List.of(openedPairDragon1, closedOrderedFourSign1, openedOrderedThreeSign1, closedOrderedThreeSign5, closedOrderedThreeSign5));

        testOneSet(List.of(MAHJONG, NO_ORDEREDS),
                List.of(openedPairDragon1, closedThreeSign3, openedThreeSign1, openedThreeSign5, openedThreeDot9));
        testOneSet(List.of(MAHJONG, NO_ORDEREDS, CLEAR_SUIT_WITH_TRUMPS),
                List.of(openedPairDragon1, closedThreeSign3, openedThreeSign1, openedThreeSign5, openedThreeSign9));

        testOneSet(new ArrayList<>(),
                List.of(closedDragon1, closedOrderedFourSign1, openedOrderedThreeSign1, closedOrderedThreeDot3, closedOrderedThreeDot7));
        testOneSet(List.of(TRUMPS_ONES_NINES, CLEAR_SUIT_WITH_TRUMPS),
                List.of(closedDragon1, openedThreeWind2, openedThreeWind3, openedThreeWind4, openedThreeDot9));
    }

    private void testOneSet(List<HandFlags.Flag> expectedFlags, List<Combination> input) {
        HandFlags gotFlags = createFrom(input, Optional.empty()).get().getKnownFlags();
        HandFlags expected = new HandFlags(expectedFlags);
        System.out.println("Expected: " + expected + ". Got: " + gotFlags);
        for (HandFlags.Flag flag: expectedFlags) {
            System.out.println("Test flag: " + flag);
            assert(gotFlags.hasFlag(flag));
        }
        assert(expected.equals(gotFlags));
    }
}
