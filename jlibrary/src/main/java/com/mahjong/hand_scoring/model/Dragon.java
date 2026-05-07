package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;

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

    /**
     * Метод-фабрика для создания дракона по словесному обозначению его цвета
     * @throws IllegalArgumentException, если введённые значения некорректны
     * */
    public static Dragon of(String name) {
        return switch (StringHelper.normalize(name)) {
            case "white", "белый" -> WHITE;
            case "red", "красный" -> RED;
            case "green", "зелёный" -> GREEN;
            default -> throw new IllegalArgumentException("Введите корректный цвет дракона");
        };
    }
}
