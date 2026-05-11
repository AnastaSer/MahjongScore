package com.mahjong.hand_scoring.utils;

import com.mahjong.hand_scoring.model.Combination;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

public class StringHelperTest {
    @Test
    public void combinationStrTest() {
        List<String> toTest = List.of("closed single dragon green",
                "closed один sign 1",
                "closed одна точка 2",
                "closed pair wind north",
                "opened three dragon red",
                "opened four dot 9",
                "closed ordered three sign 7",
                "opened ordered four bamboo 6");
        toTest.stream().forEach(this::oneCombinationStrTest);

        toTest.stream().map(s -> s + " left parts").forEach(this::oneCombinationStrTestWithLeftPart);
    }

    private void oneCombinationStrTest(String toTest) {
        Pair<String, String> nextCombination = StringHelper.combinationStr(toTest);
        assert(!nextCombination.getLeft().isEmpty());
        assert(nextCombination.getRight().isEmpty());
        assert(Combination.of(nextCombination.getLeft()).equals(Combination.of(toTest)));
    }

    private void oneCombinationStrTestWithLeftPart(String toTest) {
        String leftPart = "left parts";
        Pair<String, String> nextCombination = StringHelper.combinationStr(toTest);
        assert(!nextCombination.getLeft().isEmpty());
        assert(nextCombination.getRight().equals(leftPart));
    }
}
