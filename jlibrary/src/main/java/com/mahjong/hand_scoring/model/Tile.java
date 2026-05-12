package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Класс для описания кости.
 * Содержит перечисление всех мастей, козырей и бонусов
 * */
public record Tile(TileType type, int number) implements Comparable<Tile> {
    private static final List<String> numbers = List.of("1", "2","3","4","5","6","7","8","9");

    public enum TileType {
        SIGN, DOT, BAMBOO, WIND, DRAGON, BONUS_SEASON, BONUS_FLOWER
    }

    /**
     * Конструктор кости
     * @throws IllegalArgumentException, если значения пустые или выходят за рамки
     * Числовые значения для Мастей от 1 до 9,
     * для Драконов от 1 до 3,
     * для Ветров и бонусов от 1 до 4.
     * */
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

    /**
     * Метод-фабрика, возвращающий следующую кость в масти
     * @param first кость, для которой нужна следующая
     * @return кость со следующим числовым номером
     * @throws IllegalArgumentException, если переданная кость не масть, либо её числовое значение 9
     * */
    public static Tile next(Tile first) {
        if (!first.isSuit())
            throw new IllegalArgumentException("Следующая кость может быть только в масти, а это " + first.type);
        if (first.number == 9)
            throw new IllegalArgumentException("Следующей за 9 кости нет");
        return new Tile(first.type, first.number + 1);
    }

    /**
     * Метод-фабрика, возвращающий кость по строковому описанию
     * Первой ожидается масть, следом числовое обозначение номера кости
     * Для ветров и драконов есть возможность указать словесное обозначение
     * @throws IllegalArgumentException, если пераданные значения пусты или некорректны
     * */
    public static Tile of(String code) {
        if (code == null || StringHelper.normalize(code).isEmpty())
            throw new IllegalArgumentException("Введите масть, а через пробел номинал");
        String[] parts = StringHelper.toParts(code);
        if (parts.length != 2 && parts.length != 3)
            throw new IllegalArgumentException("Введите от двух до трёх слов");
        TileType type = switch (parts[0]) {
            case "sign", "знак", "символ" -> TileType.SIGN;
            case "dot", "дот", "точка" -> TileType.DOT;
            case "bamboo", "бамбук" -> TileType.BAMBOO;
            case "wind", "ветер" -> TileType.WIND;
            case "dragon", "дракон" -> TileType.DRAGON;
            case "bonus", "бонусный", "бонус" -> switch (parts[1]) {
                case "season", "сезон" -> TileType.BONUS_SEASON;
                case "flower", "цветок" -> TileType.BONUS_FLOWER;
                default -> throw new IllegalArgumentException("Уточните тип бонуса");
            };
            default -> throw new IllegalArgumentException("Введите первой масть " + parts[0]);
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

    /**
     * Метод, возвращающий признак, является ли данная кость бонусной
     * */
    public boolean isBonus() {
        return switch (type) {
            case BONUS_FLOWER, BONUS_SEASON -> true;
            default -> false;
        };
    }

    /**
     * Метод, возвращающий признак, является ли данная кость простой
     * */
    public boolean isSuit() {
        return switch (type) {
            case SIGN, DOT, BAMBOO -> true;
            default -> false;
        };
    }

    /**
     * Метод, возвращающий признак, является ли данная кость ветром
     * */
    public boolean isWind() {
        return type == TileType.WIND;
    }

    /**
     * Метод, возвращающий признак, является ли данная кость драконом
     * */
    public boolean isDragon() {
        return type == TileType.DRAGON;
    }

    /**
     * Метод, проверяющий, является ли данная кость предложенным ветром
     * */
    public boolean isSameWind(Wind wind) {
        return isWind() && number == wind.getValue();
    }

    /**
     * Метод, проверяющий, является ли данная кость предложенным драконом
     * */
    public boolean isSameDragon(Dragon dragon) {
        return isDragon() && number == dragon.getValue();
    }

    /**
     * Метод, проверяющий, является ли кость козырем, дающим удвоение
     * */
    public boolean isDoubleScore(Wind playerWind, Wind vipWind) {
        return isDragon() || isSameWind(playerWind) || isSameWind(vipWind);
    }

    /**
     * Метод, возвращающий стоимость открытой тройки кости
     * */
    public int countThree() {
        return isSuit() && number > 2 && number < 9 ? 2 : 4;
    }

    /**
     * Метод, возвращающий самое простое строковое описание данной кости
     * */
    public String ruStr() {
        String typeStr = switch (type) {
            case SIGN -> "знак";
            case DOT -> "дот";
            case BAMBOO -> "бамбук";
            case WIND -> "ветер";
            case DRAGON -> "дракон";
            case BONUS_SEASON -> "бонус сезон";
            case BONUS_FLOWER -> "бонус цветок";
            default -> "";
        };
        return typeStr + " " + number;
    }

    @Override
    public int compareTo(Tile o) {
        return Comparator.comparing(Tile::type)
                .thenComparing(Tile::number)
                .compare(this, o);
    }

    /**
     * Метод, проверющий, является ли переданный параметр подходящим числом.
     * */
    public static boolean isCorrectTileNumber(String number) {
        return numbers.contains(number);
    }
}
