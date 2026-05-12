package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;

import java.util.List;

/**
 * Класс для описания кости вида "Дракон"
 * */
public enum Dragon {
    WHITE(1), RED(2), GREEN(3);
    private int value;

    Dragon(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private static final List<String> whiteDragon = List.of("white", "белый");
    private static final List<String> redDragon = List.of("red", "красный");
    private static final List<String> greenDragon = List.of("green", "зелёный", "зеленый");

    /**
     * Метод-фабрика для создания дракона по словесному обозначению его цвета
     * @throws IllegalArgumentException, если введённые значения некорректны
     * */
    public static Dragon of(String name) {
        String normalName = StringHelper.normalize(name);
        if (whiteDragon.contains(normalName))
            return WHITE;
        if (redDragon.contains(normalName))
            return RED;
        if (greenDragon.contains(normalName))
            return GREEN;
        throw new IllegalArgumentException("Введите корректный цвет дракона");
    }

    /**
     * Метод, проверющий, является ли переданный параметр цветом дракона
     * */
    public static boolean isCorrectDragonName(String name) {
        return whiteDragon.contains(name) || redDragon.contains(name) || greenDragon.contains(name);
    }
}
