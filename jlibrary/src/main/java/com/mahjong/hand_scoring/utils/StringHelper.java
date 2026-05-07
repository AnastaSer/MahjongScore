package com.mahjong.hand_scoring.utils;

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
}
