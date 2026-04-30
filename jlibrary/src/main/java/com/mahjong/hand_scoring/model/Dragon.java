package com.mahjong.hand_scoring.model;

public enum Dragon {
    WHITE(1), RED(2), GREEN(3);
    private int value;

    Dragon(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Dragon of(String name) {
        return switch (name.trim().toLowerCase()) {
            case "white", "белый" -> WHITE;
            case "red", "красный" -> RED;
            case "green", "зелёный" -> GREEN;
            default -> throw new IllegalArgumentException("Введите корректный цвет дракона");
        };
    }
}
