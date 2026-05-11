package com.mahjong.hand_scoring.utils;

import com.mahjong.hand_scoring.model.Dragon;
import com.mahjong.hand_scoring.model.Tile;
import com.mahjong.hand_scoring.model.Wind;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
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

    public static String firstParts(String[] parts, int toWrite) {
        return Arrays.stream(parts).limit(toWrite).collect(Collectors.joining(" "));
    }

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

    public static boolean isTileNumber(String maybeTileNumber) {
        return Tile.isCorrectTileNumber(maybeTileNumber) || Wind.isCorrectWindName(maybeTileNumber) || Dragon.isCorrectDragonName(maybeTileNumber);
    }

    public static Pair<String, String> combinationStr(String inputStr) {
        Pair<String, String> notFound = Pair.of("", inputStr);
        String[] parts = toParts(inputStr);
        if (parts.length < 4) {
            System.out.println("Слишком мало слов для комбинации");
            return notFound;
        }
        try {
            isOpen(parts[0]);
            if (isTileNumber(parts[3]))
                return separate(parts, 4);
            else if (parts.length >= 5 && isTileNumber(parts[4]))
                return separate(parts, 5);
            else
                return notFound;
        } catch (IllegalArgumentException ignore) {
            System.out.println("Не описывает комбинацию");
            return notFound;
        }
    }

    public static Pair<String, String> inputTileStr(String inputStr) {
        return Pair.of("", inputStr);
    }

    public static Pair<String, String> handFlagStr(String inputStr) {
        return Pair.of("", inputStr);
    }
}
