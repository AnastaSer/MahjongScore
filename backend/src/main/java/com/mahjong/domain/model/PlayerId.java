package com.mahjong.domain.model;

/**
 * Задел на будущее
 * Класс для хранения информации по одному игроку
 * */
public record PlayerId(String nickname) {
    public PlayerId {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("Имя игрока должно быть заполнено");
        }
    }
}
