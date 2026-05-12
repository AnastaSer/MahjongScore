package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.ScoringCalculator;
import com.mahjong.hand_scoring.utils.StringHelper;
import com.mahjong.hand_scoring.utils.TilesHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mahjong.hand_scoring.model.HandFlags.Flag.*;

/**
 * Класс для преобразования введённых данных и расчёта очков одной руки
 * */
public class CompleteHand {
    private InputHand inputHand = null;
    private long score = 0;

    private CompleteHand() {}

    /**
     * Конструктор, принимающий собранную игроком руку и запускающий расчёт её стоимости
     * */
    public CompleteHand(InputHand inputHand) {
        if (inputHand == null)
            throw new IllegalArgumentException("Введите собранную игроком руку или используйте конструктор по умолчанию");
        this.inputHand = inputHand;
        count();
    }

    /**
     * Метод-фабрика, преобразующий массив строк в объект InputHand.
     * @param allInput введённые пользователем строки в виде массива
     * @throws IllegalArgumentException если допущена ошибка в параметрах
     * */
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

    /**
     * Метод для расчёта суммы очков за всю комбинацию с учётом ветров и флагов
     * */
    private void count() {
        score = inputHand.getScore();
        int doubles = inputHand.getDoubles();
        System.out.println("Из руки очки: " + score + ", удвоения: " + doubles);

        HandFlags allFlags = inputHand.getKnownFlags();
        if (allFlags.hasFlag(CLEAR_SUIT) || allFlags.hasFlag(TRUMPS)) {
            doubles += ScoringCalculator.getActiveRules().howManyDoublesForClearSuit();
        }
        if (allFlags.hasFlag(CLEAR_SUIT_WITH_TRUMPS)) {
            doubles++;
        }
        if (allFlags.hasFlag(TRUMPS_ONES_NINES)) {
            doubles++;
        }

        if (allFlags.hasFlag(MAHJONG)) {
            score += ScoringCalculator.getActiveRules().mahjongScore();
            if (allFlags.hasFlag(FINISHED_FROM_THE_WALL)) {
                score += 2;
            }
            if (allFlags.hasFlag(FINISHED_WITH_ONE_POSSIBLE)) {
                score += 2;
            }
            if (allFlags.hasFlag(NO_ORDEREDS)) {
                score += ScoringCalculator.getActiveRules().noOrderedAddScore();
                doubles += ScoringCalculator.getActiveRules().noOrderedDouble();
            }
            if (allFlags.hasFlag(MIZER)) {
                doubles++;
            }
            if (allFlags.hasFlag(HAS_ALL_DRAGONS)) {
                doubles++;
            }
            if (allFlags.hasFlag(HAS_ALL_WINDS)) {
                doubles++;
            }
            if (allFlags.hasFlag(WAS_WAITING_FROM_THE_START)) {
                doubles++;
            }
            if (allFlags.hasFlag(FINISHED_WITH_FREE_TILE)) {
                doubles++;
            }
            if (allFlags.hasFlag(FINISHED_WITH_LAST_IN_GAME)) {
                doubles++;
            }
            if (allFlags.hasFlag(FINISHED_BY_ROBBING_OPEN_KONG)) {
                doubles++;
            }
        }

        for (int i = 0; i < doubles; i++)
            score *= 2;

        System.out.println("Итог очки: " + score + ", удвоения: " + doubles);

        if (ScoringCalculator.getActiveRules().maximumOneHandScore().isPresent()) {
            score = Math.min(score, ScoringCalculator.getActiveRules().maximumOneHandScore().get());
        }
    }
}
