package com.mahjong.hand_scoring.model;

import javax.swing.*;
import java.util.Locale;

public record Tile(TileType type, int number) {
    public enum TileType {
        SIGN, DOT, BAMBOO, WIND, DRAGON, BONUS_SEASON, BONUS_FLOWER
    }

    public Tile {
        if (type == null)
            throw new IllegalArgumentException("Тип обязателен к указанию");
        if (number < 1 || number > 9)
            throw new IllegalArgumentException("Номинал - число от 1 до 9");
        String incorrectMsg = switch (type) {
            case SIGN, DOT, BAMBOO -> null;
            case DRAGON -> number > 3 ? "Есть всего 3 дракона" : null;
            default -> number > 4 ? "Есть всего 4 ветра и бонуса" : null;
        };
        if (incorrectMsg != null)
            throw new IllegalArgumentException(incorrectMsg);
    }

    public static Tile of(String code) {
        if (code == null || code.trim().isEmpty())
            throw new IllegalArgumentException("Введите масть, а через пробел номинал");
        String[] parts = code.trim().toLowerCase(Locale.ROOT).split(" ");
        if (parts.length != 2 && parts.length != 3)
            throw new IllegalArgumentException("Введите от двух до трёх слов");
        TileType type = switch (parts[0]) {
            case "знак", "символ", "sign" -> TileType.SIGN;
            case "дот", "точка", "dot" -> TileType.DOT;
            case "бамбук", "bamboo" -> TileType.BAMBOO;
            case "ветер", "wind" -> TileType.WIND;
            case "дракон", "dragon" -> TileType.DRAGON;
            case "бонусный", "bonus" -> switch (parts[1]) {
                case "сезон", "season" -> TileType.BONUS_SEASON;
                case "цветок", "flower" -> TileType.BONUS_FLOWER;
                default -> throw new IllegalArgumentException("Уточните тип бонуса");
            };
            default -> throw new IllegalArgumentException("Введите первой масть");
        };
        String from = switch (type) {
            case BONUS_FLOWER, BONUS_SEASON -> parts[2];
            default -> parts[1];
        };
        try {
            return new Tile(type, Integer.decode(from));
        } catch (NumberFormatException e) {
            switch (type) {
                case SIGN, DOT, BAMBOO -> throw new IllegalArgumentException("Введите номинал числом от 1 до 9");
                case BONUS_FLOWER, BONUS_SEASON -> throw new IllegalArgumentException("Введите номинал числом от 1 до 4");
                case DRAGON -> { return new Tile(type, Dragon.of(from).getValue()); }
                case WIND -> { return new Tile(type, Wind.of(from).getValue()); }
            }
            throw new IllegalArgumentException("Введите номинал числом от 1 до 9 для масти, от 1 до 3 для дракона, от 1 до 4 для ветра");
        }
    }
}
