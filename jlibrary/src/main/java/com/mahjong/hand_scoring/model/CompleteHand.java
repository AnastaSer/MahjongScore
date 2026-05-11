package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;
import com.mahjong.hand_scoring.utils.TilesHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class CompleteHand {
    private InputHand inputHand = null;
    private long score = 0;

    private CompleteHand() {}

    private CompleteHand(InputHand inputHand) {
        if (inputHand == null)
            throw new IllegalArgumentException("Введите собранную игроком руку или используйте конструктор по умолчанию");
        this.inputHand = inputHand;
        count();
    }

    public static CompleteHand of(String[] allInput) {
        if (allInput == null || allInput.length == 0)
            return new CompleteHand();
        if (allInput.length < 2)
            throw new IllegalArgumentException("Необходимо ввести минимум два ветра и кости и/или флаги");
        Wind playerWind = Wind.of(allInput[0]);
        Wind vipWind = Wind.of(allInput[1]);
        if (allInput.length == 2) {
            InputHand newHand = new InputHand(playerWind, vipWind, null);
            return new CompleteHand(newHand);
        }
        String combinationsAndFlags = StringHelper.skipParts(allInput, 2);
        List<Combination> inputCombinations = new ArrayList<>();
        List<InputTile> inputTiles = new ArrayList<>();
        List<HandFlags.Flag> inputFlags = new ArrayList<>();
        while (!combinationsAndFlags.isEmpty()) {
            Pair<String, String> nextCombination = StringHelper.combinationStr(combinationsAndFlags);
            if (!nextCombination.getLeft().isEmpty()) {
                if (!inputTiles.isEmpty()) {
                    inputCombinations.addAll(TilesHelper.tilesToCombinations(inputTiles));
                    inputTiles.clear();
                }
                inputCombinations.add(Combination.of(nextCombination.getLeft()));
                combinationsAndFlags = nextCombination.getRight();
            } else {
                Pair<String, String> nextTile = StringHelper.inputTileStr(combinationsAndFlags);
                if (!nextTile.getLeft().isEmpty()) {
                    inputTiles.add(InputTile.of(nextTile.getLeft()));
                    combinationsAndFlags = nextTile.getRight();
                } else {
                    Pair<String, String> nextFlag = StringHelper.handFlagStr(combinationsAndFlags);
                    if (!nextFlag.getLeft().isEmpty()) {
                        if (!inputTiles.isEmpty()) {
                            inputCombinations.addAll(TilesHelper.tilesToCombinations(inputTiles));
                            inputTiles.clear();
                        }
                        inputFlags.add(HandFlags.Flag.of(nextFlag.getLeft()));
                        combinationsAndFlags = nextFlag.getRight();
                    } else {
                        throw new IllegalArgumentException("Невозможно прочитать переданные параметры: " + combinationsAndFlags);
                    }
                }
            }
        }
        if (!inputTiles.isEmpty()) {
            inputCombinations.addAll(TilesHelper.tilesToCombinations(inputTiles));
            inputTiles.clear();
        }
        return new CompleteHand(new InputHand(playerWind, vipWind, new HandFlags(inputFlags), inputCombinations));
    }

    public long getScore() {
        return score;
    }

    private void count() {
        score = 0;
    }
}
