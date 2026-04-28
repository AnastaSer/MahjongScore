package com.mahjong.domain.model;

public record PlayerId(String nickname) {
    public PlayerId {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("Имя игрока должно быть заполнено");
        }
    }
}
