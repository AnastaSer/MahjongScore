package com.mahjong.hand_scoring.utils;

import com.mahjong.hand_scoring.model.Combination;
import com.mahjong.hand_scoring.model.HandFlags;
import com.mahjong.hand_scoring.model.InputTile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringHelperTest {

    private String cutTheEnd(String holeString, String theEnd) {
        return holeString.substring(0, holeString.length() - theEnd.length() - 1);
    }

    @Test
    public void combinationStrTest() {
        List<String> toTest = List.of("closed single dragon green",
                "opened one bonus season 2",
                "closed ordered three sign 7",
                "closed один sign 1",
                "closed одна точка 2",
                "closed pair wind north",
                "opened three dragon red",
                "opened three dot 3",
                "opened four dot 9",
                "opened ordered four bamboo 6");
        toTest.stream().forEach(this::oneCombinationStrTest);
        toTest.stream().map(s -> s + " left parts").forEach(this::oneCombinationStrTestWithLeftPart);
        for (int i = 0; i < toTest.size() / 2; i++) {
            twoCombinationsStrTest(toTest.get(i * 2), toTest.get(i * 2 + 1));
        }

    }

    private void oneCombinationStrTest(String toTest) {
        System.out.println("Тестирую: " + toTest);
        Pair<String, String> nextCombination = StringHelper.combinationStr(toTest);
        assert(!nextCombination.getLeft().isEmpty());
        assert(nextCombination.getRight().isEmpty());
        assert(Combination.of(nextCombination.getLeft()).equals(Combination.of(toTest)));
    }

    private void oneCombinationStrTestWithLeftPart(String toTest) {
        System.out.println("Тестирую: " + toTest);
        String leftPart = "left parts";
        Pair<String, String> nextCombination = StringHelper.combinationStr(toTest);
        assert(!nextCombination.getLeft().isEmpty());
        assert(nextCombination.getRight().equals(leftPart));
        assert(Combination.of(nextCombination.getLeft()).equals(Combination.of(cutTheEnd(toTest, leftPart))));
    }

    private void twoCombinationsStrTest(String toTest1, String toTest2) {
        String all = toTest1 + " " + toTest2;
        System.out.println("Тестирую: " + all);
        Pair<String, String> nextCombination = StringHelper.combinationStr(all);
        assert(!nextCombination.getLeft().isEmpty());
        assert(!nextCombination.getRight().isEmpty());
        assert(Combination.of(nextCombination.getLeft()).equals(Combination.of(toTest1)));
        nextCombination = StringHelper.combinationStr(nextCombination.getRight());
        assert(!nextCombination.getLeft().isEmpty());
        assert(nextCombination.getRight().isEmpty());
        assert(Combination.of(nextCombination.getLeft()).equals(Combination.of(toTest2)));
    }

    @Test
    public void inputTileStrTest() {
        List<String> toTest = List.of(
                "closed dragon green",
                "opened bonus season 2",
                "|",
                "closed sign 1",
                "closed точка 2",
                "closed точка 2",
                "closed wind north",
                "opened dragon red",
                "opened dot 9",
                "closed sign 7",
                ",",
                "opened bamboo 6");
        toTest.stream().forEach(this::oneInputTileStrTest);
        toTest.stream().map(s -> s + " left parts").forEach(this::oneInputTileStrTestWithLeftPart);
        for (int i = 0; i < toTest.size() / 2; i++) {
            twoInputTileStrTest(toTest.get(i * 2), toTest.get(i * 2 + 1));
        }
        for (int i = 0; i < toTest.size() / 3; i++) {
            threeInputTileStrTest(toTest.get(i * 3), toTest.get(i * 3 + 1), toTest.get(i * 3 +2));
        }
    }

    private void oneInputTileStrTest(String toTest) {
        System.out.println("Тестирую: " + toTest);
        Pair<String, String> nextInputTile = StringHelper.inputTileStr(toTest);
        assert(!nextInputTile.getLeft().isEmpty());
        assert(nextInputTile.getRight().isEmpty());
        assert(InputTile.of(nextInputTile.getLeft()).equals(InputTile.of(toTest)));
    }

    private void oneInputTileStrTestWithLeftPart(String toTest) {
        System.out.println("Тестирую: " + toTest);
        String leftPart = "left parts";
        Pair<String, String> nextInputTile = StringHelper.inputTileStr(toTest);
        assert(!nextInputTile.getLeft().isEmpty());
        assert(nextInputTile.getRight().equals(leftPart));
        assert(InputTile.of(nextInputTile.getLeft()).equals(InputTile.of(cutTheEnd(toTest, leftPart))));
    }

    private void twoInputTileStrTest(String toTest1, String toTest2) {
        String all = toTest1 + " " + toTest2;
        System.out.println("Тестирую: " + all);
        Pair<String, String> nextInputTile = StringHelper.inputTileStr(all);
        assert(!nextInputTile.getLeft().isEmpty());
        assert(!nextInputTile.getRight().isEmpty());
        assert(InputTile.of(nextInputTile.getLeft()).equals(InputTile.of(toTest1)));
        nextInputTile = StringHelper.inputTileStr(nextInputTile.getRight());
        assert(!nextInputTile.getLeft().isEmpty());
        assert(nextInputTile.getRight().isEmpty());
        assert(InputTile.of(nextInputTile.getLeft()).equals(InputTile.of(toTest2)));
    }

    private void threeInputTileStrTest(String toTest1, String toTest2, String toTest3) {
        String all = toTest1 + " " + toTest2 + " " + toTest3;
        System.out.println("Тестирую: " + all);
        Pair<String, String> nextInputTile = StringHelper.inputTileStr(all);
        assert(!nextInputTile.getLeft().isEmpty());
        assert(!nextInputTile.getRight().isEmpty());
        assert(InputTile.of(nextInputTile.getLeft()).equals(InputTile.of(toTest1)));
        nextInputTile = StringHelper.inputTileStr(nextInputTile.getRight());
        assert(!nextInputTile.getLeft().isEmpty());
        assert(!nextInputTile.getRight().isEmpty());
        assert(InputTile.of(nextInputTile.getLeft()).equals(InputTile.of(toTest2)));
        nextInputTile = StringHelper.inputTileStr(nextInputTile.getRight());
        assert(!nextInputTile.getLeft().isEmpty());
        assert(nextInputTile.getRight().isEmpty());
        assert(InputTile.of(nextInputTile.getLeft()).equals(InputTile.of(toTest3)));
    }

    @Test
    public void flagStrTest() {
        List<String> toTest = List.of(
                "чистая масть",
                "чистая масть с драконами и ветрами",
                "только драконы и ветра",
                "драконы, ветра, единицы и девятки",
                "маджонг",
                "мизер",
                "без последовательностей",
                "все драконы",
                "драконы, ветра, единицы и девятки",
                "все ветра",
                "предмаджонг со старта",
                "завершение единственной возможной",
                "завершение костью со стены",
                "завершение свободной костью",
                "только драконы и ветра",
                "завершение последней доступной",
                "без последовательностей",
                "завершение ограблением открытого конга"
        );
        toTest.stream().forEach(this::oneFlagStrTest);
        toTest.stream().map(s -> s + " + left parts").forEach(this::oneFlagStrTestWithLeftPart);
        for (int i = 0; i < toTest.size() / 2; i++) {
            twoFlagsStrTest(toTest.get(i * 2), toTest.get(i * 2 + 1));
        }
        for (int i = 0; i < toTest.size() / 3; i++) {
            threeFlagsStrTest(toTest.get(i * 3), toTest.get(i * 3 + 1), toTest.get(i * 3 +2));
        }
    }

    private void oneFlagStrTest(String toTest) {
        System.out.println("Тестирую: " + toTest);
        Pair<Optional<HandFlags.Flag>, String> nextFlag = StringHelper.handFlagStr(toTest);
        System.out.println("Получили: " + nextFlag.getLeft().get() + " Остаток: \'" + nextFlag.getRight() + "\'");
        assert(!nextFlag.getLeft().isEmpty());
        assert(nextFlag.getRight().isEmpty());
        assert(nextFlag.getLeft().get().equals(HandFlags.Flag.of(toTest)));
    }

    private void oneFlagStrTestWithLeftPart(String toTest) {
        System.out.println("Тестирую: " + toTest);
        String leftPart = "left parts";
        Pair<Optional<HandFlags.Flag>, String> nextFlag = StringHelper.handFlagStr(toTest);
        System.out.println("Получили: " + nextFlag.getLeft().get() + " Остаток: \'" + nextFlag.getRight() + "\'");
        assert(!nextFlag.getLeft().isEmpty());
        assert(nextFlag.getRight().equals(leftPart));
        assert(nextFlag.getLeft().get().equals(HandFlags.Flag.of(cutTheEnd(toTest,"+ " + leftPart))));
    }

    private void twoFlagsStrTest(String toTest1, String toTest2) {
        String all = toTest1 + " + " + toTest2;
        System.out.println("Тестирую: " + all);
        Pair<Optional<HandFlags.Flag>, String> nextFlag = StringHelper.handFlagStr(all);
        assert(!nextFlag.getLeft().isEmpty());
        assert(!nextFlag.getRight().isEmpty());
        assert(nextFlag.getLeft().get().equals(HandFlags.Flag.of(toTest1)));
        nextFlag = StringHelper.handFlagStr(nextFlag.getRight());
        assert(!nextFlag.getLeft().isEmpty());
        assert(nextFlag.getRight().isEmpty());
        assert(nextFlag.getLeft().get().equals(HandFlags.Flag.of(toTest2)));
    }

    private void threeFlagsStrTest(String toTest1, String toTest2, String toTest3) {
        String all = toTest1 + " + " + toTest2 + " + " + toTest3;
        System.out.println("Тестирую: " + all);
        Pair<Optional<HandFlags.Flag>, String> nextFlag = StringHelper.handFlagStr(all);
        assert(!nextFlag.getLeft().isEmpty());
        assert(!nextFlag.getRight().isEmpty());
        assert(nextFlag.getLeft().get().equals(HandFlags.Flag.of(toTest1)));
        nextFlag = StringHelper.handFlagStr(nextFlag.getRight());
        assert(!nextFlag.getLeft().isEmpty());
        assert(!nextFlag.getRight().isEmpty());
        assert(nextFlag.getLeft().get().equals(HandFlags.Flag.of(toTest2)));
        nextFlag = StringHelper.handFlagStr(nextFlag.getRight());
        assert(!nextFlag.getLeft().isEmpty());
        assert(nextFlag.getRight().isEmpty());
        assert(nextFlag.getLeft().get().equals(HandFlags.Flag.of(toTest3)));
    }

    @Test
    public void incorrectCombinationTest() {
        String toTestErr = "opened bonus season 2";
        System.out.println("Тестирую: " + toTestErr);
        Pair<String, String> nextCombination = StringHelper.combinationStr(toTestErr);
        System.out.println(nextCombination.getLeft());
        assert(nextCombination.getLeft().isEmpty());
        assert(nextCombination.getRight().equals(toTestErr));
        String toTestCorrect = "opened one bonus season 2";
        String all = toTestCorrect + " " + toTestErr;
        System.out.println("Тестирую: " + all);
        nextCombination = StringHelper.combinationStr(all);
        assert(!nextCombination.getLeft().isEmpty());
        nextCombination = StringHelper.combinationStr(nextCombination.getRight());
        assert(nextCombination.getLeft().isEmpty());
        assert(nextCombination.getRight().equals(toTestErr));
    }

    @Test
    public void incorrectInputTileTest() {
        String toTestCorrect = "closed dragon green";
        String toTestErr = "чистая масть";
        System.out.println("Тестирую: " + toTestErr);
        Pair<String, String> nextInputTile = StringHelper.inputTileStr(toTestErr);
        assert(nextInputTile.getLeft().isEmpty());
        assert(nextInputTile.getRight().equals(toTestErr));
        String all = toTestCorrect + " " + toTestErr;
        System.out.println("Тестирую: " + all);
        nextInputTile = StringHelper.inputTileStr(all);
        assert(!nextInputTile.getLeft().isEmpty());
        assert(!nextInputTile.getRight().isEmpty());
        assert(InputTile.of(nextInputTile.getLeft()).equals(InputTile.of(toTestCorrect)));
        nextInputTile = StringHelper.inputTileStr(nextInputTile.getRight());
        assert(nextInputTile.getLeft().isEmpty());
        assert(nextInputTile.getRight().equals(toTestErr));
    }
}
