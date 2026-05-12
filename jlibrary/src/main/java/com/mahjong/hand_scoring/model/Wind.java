package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;

import java.util.List;

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

    private static final List<String> eastWinds = List.of("east", "восток", "восточный");
    private static final List<String> southWinds = List.of("south", "юг", "южный");
    private static final List<String> westWinds = List.of("west", "запад", "западный");
    private static final List<String> northWinds = List.of("north", "север", "северный");

    /**
     * Метод-фабрика для создания ветра по словесному обозначению его направления
     * @throws IllegalArgumentException, если введённые значения некорректны
     * */
    public static Wind of(String name) {
        String normalName = StringHelper.normalize(name);
        if (eastWinds.contains(normalName))
            return EAST;
        if (southWinds.contains(normalName))
            return SOUTH;
        if (westWinds.contains(normalName))
            return WEST;
        if (northWinds.contains(normalName))
            return NORTH;
        throw new IllegalArgumentException("Введите корректное направление ветра");
    }

    /**
     * Метод, проверющий, является ли переданный параметр направлением ветра
     * */
    public static boolean isCorrectWindName(String name) {
        return eastWinds.contains(name) || southWinds.contains(name)
                || westWinds.contains(name) || northWinds.contains(name);
    }
}
