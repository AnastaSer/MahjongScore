package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;

/**
 * Класс для описания кости вида "Ветер"
 * */
public enum Wind {
    EAST(1), SOUTH(2), WEST(3), NORTH(4);
    private int value;

    Wind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Метод-фабрика для создания ветра по словесному обозначению его направления
     * @throws IllegalArgumentException, если введённые значения некорректны
     * */
    public static Wind of(String name) {
        return switch (StringHelper.normalize(name)) {
            case "east", "восток", "восточный" -> EAST;
            case "south", "юг", "южный" -> SOUTH;
            case "west", "запад", "западный" -> WEST;
            case "north", "север", "северный" -> NORTH;
            default -> throw new IllegalArgumentException("Введите корректное направление ветра");
        };
    }
}
