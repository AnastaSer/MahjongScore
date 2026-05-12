package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;
import com.mahjong.hand_scoring.utils.TilesHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (allInput == null || allInput.length == 0 || (allInput.length == 1 && allInput[0].isEmpty()))
            return new CompleteHand();
        if (allInput.length < 2)
            throw new IllegalArgumentException("Ошибка в параметрах: Необходимо ввести минимум два ветра и кости и/или флаги");
        Wind playerWind;
        Wind vipWind;
        try {
            playerWind = Wind.of(allInput[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка в параметрах: '" + allInput[0] + "'. А именно: " + e.getMessage());
        }
        try {
            vipWind = Wind.of(allInput[1]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка в параметрах: '" + allInput[1] + "'. А именно: " + e.getMessage());
        }
        if (allInput.length == 2) {
            InputHand newHand = new InputHand(playerWind, vipWind, null);
            return new CompleteHand(newHand);
        }
        String combinationsAndFlags = StringHelper.skipParts(allInput, 2);
        List<Combination> inputCombinations = new ArrayList<>();
        List<InputTile> inputTiles = new ArrayList<>();
        List<HandFlags.Flag> inputFlags = new ArrayList<>();
        try {
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
                        Pair<Optional<HandFlags.Flag>, String> nextFlag = StringHelper.handFlagStr(combinationsAndFlags);
                        if (!inputTiles.isEmpty()) {
                            inputCombinations.addAll(TilesHelper.tilesToCombinations(inputTiles));
                            inputTiles.clear();
                        }
                        inputFlags.add(nextFlag.getLeft().get());
                        combinationsAndFlags = nextFlag.getRight();
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка в параметрах: '" + combinationsAndFlags + "'. А именно: " + e.getMessage());
        }
        if (!inputTiles.isEmpty()) {
            inputCombinations.addAll(TilesHelper.tilesToCombinations(inputTiles));
            inputTiles.clear();
        }
        return new CompleteHand(new InputHand(playerWind, vipWind, new HandFlags(inputFlags), inputCombinations));
    }

    public InputHand getInputHand() {
        return inputHand;
    }

    public long getScore() {
        return score;
    }

    private void count() {
        score = 0;
    }
}
