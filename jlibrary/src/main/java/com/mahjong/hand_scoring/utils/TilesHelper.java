package com.mahjong.hand_scoring.utils;

import com.mahjong.hand_scoring.ScoringCalculator;
import com.mahjong.hand_scoring.model.Combination;
import com.mahjong.hand_scoring.model.InputTile;
import com.mahjong.hand_scoring.model.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для преобразования списка, полученных от пользователя костей, в список комбинаций
 * */
public class TilesHelper {
    /**
     * Преобразование списка костей от пользователя в список комбинаций.
     * Перечень костей определяет их анализ.
     * Если передать подряд три кости, а потом повторить дважды последнюю, то будут засчитаны последовательность и пара.
     * Чтобы разделить комбинации, необходимо использовать сепараторы вместо описания кости: "|", ",", ";"
     * Так же разделяются комбинации при перемене статуса костей с "открытых" на "закрытые"
     * И при выпадении кости из возможной комбинации (другой козырь, бонус, кость другой масти, кость, прерывающая цепочку по возрастанию)
     * @throws IllegalArgumentException, если полученные не последовательные комбинации совпадают один в один
     * */
    public static List<Combination> tilesToCombinations(List<InputTile> inputTiles) {
        if (inputTiles == null || inputTiles.isEmpty())
            return new ArrayList<>();
        List<Combination> combinations = new ArrayList<>();
        List<InputTile> analyze = new ArrayList<>();
        for (InputTile inputTile: inputTiles) {
            if (inputTile.type().equals(InputTile.InputTileType.SEPARATOR)) {
                addAnalyzed(analyze, combinations);
            } else {
                Tile nextTile = inputTile.tile();
                if (nextTile.isBonus()) {
                    addAnalyzed(analyze, combinations);
                    addCombination(new Combination(Combination.CombinationType.SINGLE, true, inputTile.tile()), combinations);
                } else if (analyze.isEmpty()) {
                    analyze.add(inputTile);
                } else {
                    InputTile analyzeFirst = analyze.getFirst();
                    InputTile analyzeLast = analyze.getLast();
                    if (inputTile.equals(analyzeFirst) && inputTile.equals(analyzeLast)) {
                        analyze.add(inputTile);
                        if (analyze.size() == 4) {
                            addCombination(new Combination(Combination.CombinationType.FOUR, inputTile.isOpen(), inputTile.tile()), combinations);
                            analyze.clear();
                        }
                    } else if ((analyzeFirst.isOpen() == inputTile.isOpen())
                            && analyzeFirst.tile().type().equals(nextTile.type())
                            && nextTile.isSuit()
                            && (nextTile.number() == (analyzeLast.tile().number() + 1))) {
                        if (analyze.size() == 3) {
                            if (ScoringCalculator.getActiveRules().canUseOrderedFour()) {
                                addCombination(new Combination(Combination.CombinationType.ORDERED_FOUR, inputTile.isOpen(), analyzeFirst.tile()), combinations);
                                analyze.clear();
                            } else {
                                addCombination(new Combination(Combination.CombinationType.ORDERED_THREE, inputTile.isOpen(), analyzeFirst.tile()), combinations);
                                analyze.clear();
                                analyze.add(inputTile);
                            }
                        } else {
                            analyze.add(inputTile);
                        }
                    } else {
                        addAnalyzed(analyze, combinations);
                        analyze.add(inputTile);
                    }
                }
            }
        }
        addAnalyzed(analyze, combinations);

        return combinations;
    }

    private static List<Combination> fromAnalyze(List<InputTile> analyze) {
        if (analyze.isEmpty())
            return List.of();
        boolean isOpen = analyze.getFirst().isOpen();
        Tile first = analyze.getFirst().tile();
        return switch (analyze.size()) {
            case 1 -> List.of(new Combination(Combination.CombinationType.SINGLE, isOpen, first));
            case 2 -> {
                if (first.equals(analyze.get(1).tile()))
                    yield List.of(new Combination(Combination.CombinationType.PAIR, isOpen, first));
                else
                    yield List.of(new Combination(Combination.CombinationType.SINGLE, isOpen, first),
                            new Combination(Combination.CombinationType.SINGLE, isOpen, analyze.get(1).tile()));
            }
            case 3 ->  {
                if (first.equals(analyze.get(1).tile()))
                    yield List.of(new Combination(Combination.CombinationType.THREE, isOpen, first));
                else
                    yield List.of(new Combination(Combination.CombinationType.ORDERED_THREE, isOpen, first));
            }
            default -> throw new IllegalArgumentException("Нельзя копить более 3 фишек для анализа");
        };
    }

    private static void addCombination(Combination newOne, List<Combination> gotCombinations) {
        if (newOne.type().isOrdered() || !gotCombinations.contains(newOne))
            gotCombinations.add(newOne);
        else
            throw new IllegalArgumentException("В игре не может быть одинаковых комбинаций");
    }

    private static void addAnalyzed(List<InputTile> analyze, List<Combination> combinations) {
        for (Combination readyOne: fromAnalyze(analyze)) {
            addCombination(readyOne, combinations);
        }
        if (!analyze.isEmpty())
            analyze.clear();
    }
}
