package com.mahjong.hand_scoring.utils;

import com.mahjong.hand_scoring.model.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Класс с набором методов, облегчающих и типизирующих работу со строками
 * */
public class StringHelper {
    /**
     * Преобразование строки в логическую переменную по признаку "открытый"/"закрытый"
     * @throws IllegalArgumentException, если введённые значения пусты или некорректны
     * */
    public static boolean isOpen(String isOpenStr) {
        if (isOpenStr == null || normalize(isOpenStr).isEmpty())
            throw new IllegalArgumentException("Введите описание: открытая или закрытая?");
        return switch (isOpenStr) {
            case "opened", "открытая", "открытый" -> true;
            case "closed", "закрытая", "закрытый" -> false;
            default -> throw new IllegalArgumentException("Уточните, открытая или закрытая?");
        };
    }

    /**
     * Проверка, является ли переданная строка разделителем при анализе последовательного набора костей
     * @throws IllegalArgumentException, если введённые значения пусты или некорректны
     * */
    public static boolean isSeparator(String input) {
        if (input == null || normalize(input).isEmpty())
            throw new IllegalArgumentException("Ничего не передано");
        return switch (StringHelper.normalize(input)) {
            case ";", "|", ",", "separator", "разделитель" -> true;
            default -> false;
        };
    }

    /**
     * Преобразует массив строк в строку, разделённую пробелами, пропустив первые @toSkip слов
     * @param parts массив строк
     * @param toSkip число слов, что необходимо пропустить
     * */
    public static String skipParts(String[] parts, int toSkip) {
        return Arrays.stream(parts).skip(toSkip).collect(Collectors.joining(" "));
    }

    /**
     * Преобразует массив строк в строку, содержащую первые @toWrite слов
     * @param parts массив строк
     * @param toWrite число слов, что необходимо записать
     * */
    public static String firstParts(String[] parts, int toWrite) {
        return Arrays.stream(parts).limit(toWrite).collect(Collectors.joining(" "));
    }

    /**
     * Преобразует массив строк в две строки. В первой - слова до @where. Во второй - все остальные.
     * @param parts массив строк
     * @param where число слов, что необходимо записать в первую часть
     * */
    public static Pair<String, String> separate(String[] parts, int where) {
        return Pair.of(firstParts(parts, where), skipParts(parts, where));
    }

    /**
     * Преобразует массив строк в список строк, пропустив первое
     * */
    public static List<String> cutFirst(String[] parts) {
        return Arrays.stream(parts).skip(1).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Преобразует строку в массив строк, используя пробел в качестве разделителя
     * */
    public static String[] toParts(String input) {
        return normalize(input).split(" ");
    }

    /**
     * Преобразует строку к единообразному виду - без лишних пробелов и в нижнем регистре
     * */
    public static String normalize(String input) {
        return input.trim().replace("  ", " ").toLowerCase();
    }

    /**
     * Проверка, является ли @maybeTileNumber числом, ветром или драконом
     * */
    public static boolean isTileNumber(String maybeTileNumber) {
        return Tile.isCorrectTileNumber(maybeTileNumber) || Wind.isCorrectWindName(maybeTileNumber) || Dragon.isCorrectDragonName(maybeTileNumber);
    }

    /**
     * Проверка, является ли @maybeCombination корректным описанием комбинации
     * */
    public static boolean isCombination(String maybeCombination) {
        return Combination.CombinationType.isCorrectCombination(maybeCombination);
    }

    /**
     * Отделение описания комбинации из переданной строки.
     * @return Пару строк: описание комбинации и остаток переданной строки.
     * Если невозможно выделить описание комбинации, вернёт пустую строку и изначальную.
     * */
    public static Pair<String, String> combinationStr(String inputStr) {
        Pair<String, String> notFound = Pair.of("", inputStr);
        String[] parts = toParts(inputStr);
        if (parts.length < 4) {
            return notFound;
        }
        try {
            isOpen(parts[0]);
            if (isCombination(parts[1])) {
                if (isTileNumber(parts[3]))
                    return separate(parts, 4);
                else if (parts.length >= 5 && isTileNumber(parts[4]))
                    return separate(parts, 5);
            }
            return notFound;
        } catch (IllegalArgumentException ignore) {
            return notFound;
        }
    }

    /**
     * Отделение описания одной кости из переданной строки.
     * @return Пару строк: описание кости и остаток переданной строки.
     * Если невозможно выделить описание кости, вернёт пустую строку и изначальную.
     * */
    public static Pair<String, String> inputTileStr(String inputStr) {
        Pair<String, String> notFound = Pair.of("", inputStr);
        String[] parts = toParts(inputStr);
        if (parts.length < 1) {
            return notFound;
        }
        if (isSeparator(parts[0])) {
            return separate(parts, 1);
        }
        try {
            isOpen(parts[0]);
            if (isTileNumber(parts[2]))
                return separate(parts, 3);
            else if (parts.length >= 4 && isTileNumber(parts[3]))
                return separate(parts, 4);
            else
                return notFound;
        } catch (IllegalArgumentException ignore) {
            return notFound;
        }
    }

    /**
     * Отделение описания одного флага из переданной строки.
     * Разделитель  ' + '
     * @return Пару: Флаг и остаток переданной строки.
     * @throws IllegalArgumentException если переданная строка пуста или не может быть преобразована во флаг
     * */
    public static Pair<Optional<HandFlags.Flag>, String> handFlagStr(String inputStr) {
        int separatorIndex = inputStr.indexOf("+");
        if (separatorIndex > 0) {
            HandFlags.Flag flag = HandFlags.Flag.of(normalize(inputStr.substring(0, separatorIndex)));
            return Pair.of(Optional.of(flag), normalize(inputStr.substring(separatorIndex + 1)));
        } else {
            HandFlags.Flag flag = HandFlags.Flag.of(normalize(inputStr));
            return Pair.of(Optional.of(flag), "");
        }
    }
}
