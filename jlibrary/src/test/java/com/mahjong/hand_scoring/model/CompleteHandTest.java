package com.mahjong.hand_scoring.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CompleteHandTest {
    private static final Rules activeRules = RulesSet.load();
    private Optional<CompleteHand> createFrom(String describeAll) {
        try {
            return Optional.of(CompleteHand.of(describeAll.split(" "), activeRules));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<CompleteHand> createFrom(String describeAll, Rules activeRules) {
        try {
            System.out.println("Try create with rules: " + activeRules.name());
            return Optional.of(CompleteHand.of(describeAll.split(" "), activeRules));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Test
    public void correctCountTest() {
        Map<String, Integer> correctInputs = Map.of(
                "", 0,
                "восточный северный", 0,
                "восточный северный closed ordered three sign 7 чистая масть + маджонг", 160,
                "северный восточный маджонг", 20,
                "восточный северный открытая пара дракон зеленый закрытая тройка дракон красный закрытая четверка дракон белый закрытая тройка ветер восточный закрытая четвёрка ветер северный", 2000
        );

        correctInputs.keySet().stream().forEach(input -> {
            System.out.println("Тестирую: '" + input + "' ");
            Optional<CompleteHand> hand = createFrom(input);
            assert(!hand.isEmpty());
            assert(hand.get().getScore() == correctInputs.get(input));
            });

        Map<String, Integer> correctFriendsInputs = Map.of(
                "", 0,
                "восточный северный", 0,
                "восточный северный closed ordered three sign 7 чистая масть + маджонг", 640,
                "северный восточный маджонг", 20,
                "восточный северный открытая пара дракон зеленый закрытая тройка дракон красный закрытая четверка дракон белый закрытая тройка ветер восточный закрытая четвёрка ветер северный", 81920
        );

        correctFriendsInputs.keySet().stream().forEach(input -> {
            System.out.println("Тестирую: '" + input + "' ");
            Optional<CompleteHand> hand = createFrom(input, RulesSet.of("друзья"));
            assert(!hand.isEmpty());
            assert(hand.get().getScore() == correctFriendsInputs.get(input));
        });

        Map<String, Integer> correctParentsInputs = Map.of(
                "", 0,
                "восточный северный", 0,
                "восточный северный closed ordered three sign 7 чистая масть + маджонг", 160,
                "северный восточный маджонг", 20,
                "восточный северный открытая пара дракон зеленый закрытая тройка дракон красный закрытая четверка дракон белый закрытая тройка ветер восточный закрытая четвёрка ветер северный", 35840
        );

        correctParentsInputs.keySet().stream().forEach(input -> {
            System.out.println("Тестирую: '" + input + "' ");
            Optional<CompleteHand> hand = createFrom(input, RulesSet.of("родители"));
            assert(!hand.isEmpty());
            assert(hand.get().getScore() == correctParentsInputs.get(input));
        });
    }

    @Test
    public void incorrectTest() {
        List<String> correctInputs = List.of(
                "восточный",
                "closed ordered three sign 7 чистая масть + маджонг",
                "восточный closed ordered three sign 7 чистая масть + маджонг",
                "восточный северный closed ordered sign 7 чистая масть + маджонг",
                "восточный северный closed ordered three sign 7 совсем чистая масть + маджонг"
        );

        correctInputs.stream().forEach(s-> {
            System.out.println("Тестирую: '" + s + "'");
            Optional<CompleteHand> hand = createFrom(s);
            assert(hand.isEmpty());
        });
    }
}
