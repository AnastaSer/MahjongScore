package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;

/**
 * Класс для хранения собранных костей в виде комбинаций.
 * Содержит перечисление с типами комбинаций.
 * */
public record Combination(CombinationType type, Boolean isOpen, Tile tile) implements Comparable<Combination> {

    public enum CombinationType {
        SINGLE, PAIR, THREE, FOUR, ORDERED_THREE, ORDERED_FOUR;

        private static final List<String> singleStr = List.of("one", "single", "один", "одна");
        private static final List<String> pairStr = List.of("pair", "two", "пара", "два", "две");
        private static final List<String> threeStr = List.of("three", "панг", "тройка", "три");
        private static final List<String> fourStr = List.of("four", "конг", "четверка", "четвёрка", "четыре");
        private static final List<String> orderedStr = List.of("ordered", "чоу", "последовательность", "подряд");

        public static CombinationType of(List<String> parts) {
            if (parts.size() < 1)
                throw new IllegalArgumentException("Введите описание комбинации");
            String part0 = parts.get(0);
            if (singleStr.contains(part0)) return SINGLE;
            if (pairStr.contains(part0)) return PAIR;
            if (threeStr.contains(part0)) return THREE;
            if (fourStr.contains(part0)) return FOUR;
            if (orderedStr.contains(part0)) {
                    if (parts.size() < 2)
                        throw new IllegalArgumentException("Введите вид последовательности");
                    return switch (parts.get(1)) {
                        case "three", "три", "база", "классика" -> ORDERED_THREE;
                        case "four", "четыре", "расширение" -> ORDERED_FOUR;
                        default -> throw new IllegalArgumentException("Введите число костей в последовательности");
                    };
            }
            throw new IllegalArgumentException("Введите корректную комбинацию");
        }

        public boolean isOrdered() {
            return switch (this) {
                case ORDERED_FOUR, ORDERED_THREE -> true;
                default -> false;
            };
        }

        /**
         * Метод, проверющий, является ли переданный параметр корректной комбинацией
         * */
        public static boolean isCorrectCombination(String name) {
            return singleStr.contains(name) || pairStr.contains(name)
                    || threeStr.contains(name) || fourStr.contains(name)
                    || orderedStr.contains(name);
        }

    }

    /**
     * Конструктор
     * @throws IllegalArgumentException, если введённые значения пусты или противоречат логике игры
     * */
    public Combination {
        if (type == null)
            throw new IllegalArgumentException("Введите тип комбинации");
        if (tile == null)
            throw new IllegalArgumentException("Введите кость");
        if (tile.isBonus() && type != CombinationType.SINGLE)
            throw new IllegalArgumentException("Бонусы могут быть только по одному");
        if (type.isOrdered()) {
            if (!tile.isSuit())
                throw new IllegalArgumentException("В последовательность собирают только масти");
            switch (type) {
                case ORDERED_THREE -> {
                    if (tile.number() > 7)
                        throw new IllegalArgumentException("Младшая кость классической последовательности не может быть старше 7");
                }
                case ORDERED_FOUR -> {
                    if (tile.number() > 6)
                        throw new IllegalArgumentException("Младшая кость расширенной последовательности не может быть старше 6");
                }
            }
        }
        if (tile.isBonus()) {
            if (!isOpen) throw new IllegalArgumentException("Бонусы могут быть только открытыми");
        } else if (type.equals(CombinationType.SINGLE)) {
            if (isOpen) throw new IllegalArgumentException("Одиночные кости могут быть только закрытыми");
        }

    }

    /**
     * Метод-фабрика для создания комбинаций по словесному описанию
     * Использование: вид комбинации кость
     * @throws IllegalArgumentException, если введённые значения некорректны или противоречат логике игры
     * */
    public static Combination of(String input) {
        if (input == null || StringHelper.normalize(input).isEmpty())
            throw new IllegalArgumentException("Введите тип комбинации");
        String[] parts = StringHelper.toParts(input);
        if (parts.length < 4)
            throw new IllegalArgumentException("Введите вид комбинации, а через пробел описание кости");
        boolean isOpen = StringHelper.isOpen(parts[0]);
        CombinationType type = CombinationType.of(StringHelper.cutFirst(parts));
        int toSkip = switch (type) {
            case ORDERED_FOUR, ORDERED_THREE -> 3;
            default -> 2;
        };
        return new Combination(type, isOpen, Tile.of(StringHelper.skipParts(parts, toSkip)));
    }

    /**
     * Метод-фабрика для создания комбинаций по словесному описанию
     * Использование: строка - вид комбинации, объект Tile
     * @throws IllegalArgumentException, если введённые значения некорректны или противоречат логике игры
     * */
    public static Combination of(String combinationStr, Tile tile) {
        if (combinationStr == null || StringHelper.normalize(combinationStr).isEmpty())
            throw new IllegalArgumentException("Введите тип комбинации");
        String[] parts = StringHelper.toParts(combinationStr);
        if (parts.length < 2)
            throw new IllegalArgumentException("Введите вид комбинации");
        boolean isOpen = StringHelper.isOpen(parts[0]);
        CombinationType type = CombinationType.of(StringHelper.cutFirst(parts));
        return new Combination(type, isOpen, tile);
    }

    /**
     * Метод, рассчитывающий очки за комбинацию и число удвоений за неё
     * */
    public Pair<Integer, Integer> countForWind(Wind playerWind, Wind vipWind) {
        Pair<Integer, Integer> noScores = Pair.of(0, 0);
        boolean isDouble = tile.isDoubleScore(playerWind, vipWind);
        return switch (type) {
            case ORDERED_FOUR, ORDERED_THREE -> noScores;
            case SINGLE -> tile.isBonus() ? Pair.of(4, tile.number() == playerWind.getValue() ? 1 : 0) : noScores;
            case PAIR ->  isDouble ? Pair.of(2, 0) : noScores;
            case THREE -> Pair.of(tile.countThree() * (isOpen ? 1 : 2), isDouble ? 1 : 0);
            case FOUR -> Pair.of(tile.countThree() * (isOpen ? 1 : 2) * 2, isDouble? 1 : 0);
        };
    }

    @Override
    public int compareTo(Combination o) {
        return Comparator.comparing(Combination::type)
                .thenComparing(Combination::tile)
                .compare(this, o);
    }
}
