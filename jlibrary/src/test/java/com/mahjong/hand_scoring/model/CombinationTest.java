package com.mahjong.hand_scoring.model;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CombinationTest {
    private final static Tile tileToCompare = new Tile(Tile.TileType.SIGN, 1);

    private Optional<Combination> createFrom(String describe, Optional<Tile> tile) {
        try {
            if (tile.isEmpty())
                return Optional.of(Combination.of(describe));
            else
                return Optional.of(Combination.of(describe, tile.get()));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Test
    public void incorrectCombinations() {
        assertThrows(IllegalArgumentException.class, () -> Combination.of(null, null));
        assertThrows(IllegalArgumentException.class, () -> Combination.of("пара", null));
        assertThrows(IllegalArgumentException.class, () -> Combination.of("пара", Tile.of(null)));

        List<String> incorrectInputs = List.of("opened пара bonus season 1",
                "closed single red dragon",
                "opened просто дот 2",
                "точка 6",
                "closed ordered five sign 3",
                "opened ordered бамбук 7");

        incorrectInputs.stream().forEach(s-> {
            System.out.println("Тестирую: '" + s + "'");
            assert(createFrom(s, Optional.empty()).isEmpty());
        });

        Tile bonusFlower = new Tile(Tile.TileType.BONUS_FLOWER, 1);
        Tile bonusSeason = new Tile(Tile.TileType.BONUS_SEASON, 2);
        List<Tile> bonuses = List.of(bonusFlower, bonusSeason);
        Tile wind1 = new Tile(Tile.TileType.WIND, 1);
        Tile dragon3 = new Tile(Tile.TileType.DRAGON, 3);
        Tile sign7 = new Tile(Tile.TileType.SIGN, 7);
        Tile sign8 = new Tile(Tile.TileType.SIGN, 8);
        Tile sign9 = new Tile(Tile.TileType.SIGN, 9);
        List<Tile> singleSimpleTile = List.of(sign7);

        Map<String, List<Tile>> incorrectInputPairs = Map.of(
                "просто", singleSimpleTile,
                "opened пара", bonuses,
                "closed ordered", singleSimpleTile,
                "opened ordered five", singleSimpleTile,
                "closed three", bonuses,
                "opened four", bonuses,
                "closed ordered three", List.of(bonusFlower, bonusFlower, wind1, dragon3, sign8, sign9),
                "opened ordered four", List.of(bonusFlower, bonusFlower, wind1, dragon3, sign7, sign8, sign9)
        );
        incorrectInputPairs.keySet().stream().forEach(description -> {
            System.out.println("Тестирую: '" + description + "'");
            incorrectInputPairs.get(description).stream().forEach(t -> {
                System.out.println(t);
                assert(createFrom(description, Optional.of(t)).isEmpty());
            });
        });
    }

    @Test
    public void correctCombinations() {
        List<String> correctInputs = List.of("closed single dragon green",
                "closed один sign 1",
                "closed одна точка 2",
                "closed pair wind north",
                "opened three dragon red",
                "opened four dot 9",
                "closed ordered three sign 7",
                "opened ordered four bamboo 6",
                "закрытая четвёрка ветер северный");
        correctInputs.stream().forEach(s-> {
            System.out.println("Тестирую: '" + s + "'");
            assert(!createFrom(s, Optional.empty()).isEmpty());
        });

        Tile bonusFlower = new Tile(Tile.TileType.BONUS_FLOWER, 1);
        Tile bonusSeason = new Tile(Tile.TileType.BONUS_SEASON, 2);
        Tile wind1 = new Tile(Tile.TileType.WIND, 1);
        Tile dragon2 = new Tile(Tile.TileType.DRAGON, 2);
        Tile sign5 = new Tile(Tile.TileType.SIGN, 5);
        Tile sign6 = new Tile(Tile.TileType.SIGN, 6);
        Tile sign7 = new Tile(Tile.TileType.SIGN, 7);
        List<Tile> all = List.of(bonusFlower, bonusSeason, wind1, dragon2, sign7);
        List<Tile> notBonuses = List.of(wind1, dragon2, sign6);

        Map<String, List<Tile>> correctInputPairs = Map.of(
                "closed single", notBonuses,
                "opened single", List.of(bonusFlower, bonusSeason),
                "closed pair", notBonuses,
                "opened three", notBonuses,
                "closed four", notBonuses,
                "opened ordered three", List.of(sign5, sign6, sign7),
                "closed ordered four", List.of(sign5, sign6)
        );
        correctInputPairs.keySet().stream().forEach(description -> {
                    System.out.println("Тестирую: '" + description + "' ");
                    correctInputPairs.get(description).stream().forEach(t ->{
                        System.out.println(t);
                        assert(!createFrom(description, Optional.of(t)).isEmpty());
                    });
        });
    }

    @Test
    public void equalCombinations() {
        testEqual(new Combination(Combination.CombinationType.SINGLE, false, tileToCompare),
                List.of("single", "one", "один", "одна"));
        testEqual(new Combination(Combination.CombinationType.PAIR, true, tileToCompare),
                List.of("pair", "two", "пара", "два", "две"));
        testEqual(new Combination(Combination.CombinationType.THREE, true, tileToCompare),
                List.of("three", "панг", "тройка", "три"));
        testEqual(new Combination(Combination.CombinationType.FOUR, true, tileToCompare),
                List.of("four", "конг", "четверка", "четыре"));
        testOrdered(Combination.CombinationType.ORDERED_THREE, List.of("three", "три", "база", "классика"));
        testOrdered(Combination.CombinationType.ORDERED_FOUR, List.of("four", "четыре", "расширение"));
    }

    private void testEqual(Combination origin, List<String> sameNames) {
        String prefix = origin.isOpen() ? "opened " : "closed ";
        System.out.println("Тестирую: " + origin);
        sameNames.stream().
                map(s -> {
                    String buf = prefix + s + " " + tileToCompare.ruStr();
                    System.out.println("Тестирую: '" + buf + "'");
                    return Combination.of(buf);
                }).forEach(copy -> assertEquals(copy, origin));
        sameNames.stream().
                map(s -> {
                    String buf = prefix + s;
                    System.out.println("Тестирую: '" + buf + "'");
                    return Combination.of(buf, tileToCompare);
                }).forEach(copy -> assertEquals(copy, origin));
    }


    private void testOrdered(Combination.CombinationType type, List<String> orderedNames) {
        List<String> ordered = List.of("ordered", "чоу", "последовательность", "подряд");
        List<String> allThree = new ArrayList<>();
        ordered.stream().forEach(start -> allThree.addAll(orderedNames.stream().
                map(finish -> start + " " + finish).collect(Collectors.toUnmodifiableList())));
        testEqual(new Combination(type, true, tileToCompare), allThree);
    }
}
