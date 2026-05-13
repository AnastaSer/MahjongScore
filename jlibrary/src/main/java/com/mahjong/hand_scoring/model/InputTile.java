package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;

/**
 * Класс, описывающий переданную пользователем кость.
 * Расширяет описание кости признаком "открытая"/"закрытая"
 * Может быть не костью, а разделителем
 * */
public record InputTile(InputTileType type, boolean isOpen, Tile tile) {
    public enum InputTileType {
        TILE, SEPARATOR
    }

    /**
     * Конструктор
     * @throws IllegalArgumentException если задан тип TILE, но кость не определена
     * */
    public InputTile {
        if (type == InputTileType.TILE && tile == null)
            throw new IllegalArgumentException("Введите кость");
    }

    /**
     * Метод-фабрика, возвращающий пользовательскую кость либо разделитель по строковому описанию
     * @throws IllegalArgumentException если нет описания кости, либо описание некорректно
     * */
    public static InputTile of(String inputTileStr) {
        if (inputTileStr == null || StringHelper.normalize(inputTileStr).isEmpty())
            throw new IllegalArgumentException("Введите описание кости");
        String[] parts = StringHelper.toParts(inputTileStr);
        if (parts.length == 1) {
            if (StringHelper.isSeparator(parts[0]))
                return new InputTile(InputTileType.SEPARATOR,false, null);
            else
                throw new IllegalArgumentException("Введите кость или разделитель");
        }
        if (parts.length < 3)
            throw new IllegalArgumentException("Введите открыта кость или закрыта и её описание");
        return new InputTile(InputTileType.TILE, StringHelper.isOpen(parts[0]),
                Tile.of(StringHelper.skipParts(parts, 1)));
    }

    /**
     * Метод-фабрика, создающий пользовательскую кость с типом TILE по строковому описанию открытости и кости
     * @throws IllegalArgumentException при некорректном описании флага или кости.
     * */
    public static InputTile of(String isOpenStr, Tile tile) {
        return new InputTile(InputTileType.TILE, StringHelper.isOpen(isOpenStr), tile);
    }

    /**
     * Метод-фабрика, создающий пользовательскую кость с типом TILE
     * @throws IllegalArgumentException при некорректном описании флага или кости.
     * */
    public static InputTile of(boolean isOpen, Tile tile) {
        return new InputTile(InputTileType.TILE, isOpen, tile);
    }

    /**
     * Метод-фабрика, возвращающий разделитель
     * */
    public static InputTile separator() {
        return new InputTile(InputTileType.SEPARATOR, false, null);
    }
}
