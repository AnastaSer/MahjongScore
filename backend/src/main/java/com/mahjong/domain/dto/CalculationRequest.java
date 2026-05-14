package com.mahjong.domain.dto;

import java.util.List;

/**
 * Запрос на расчёт очков.
 * Клиент (Android/Web) присылает JSON, который превращается в этот объект.
 */
public record CalculationRequest(
        /**
         * Ветер игрока
         * */
        String playerWind,

        /**
         * Преимущественный ветер
         * */
        String vipWind,

        /**
         * Комбинации или кости
         * Может быть null или пустым списком
         */
        List<String> combinationsOrTiles,

        /**
         * Флаги
         * Может быть null или пустым списком
         */
        List<String> flags,

        /**
         * Какой набор правил использовать: "classic", "parents", "friends"
         * Может быть null
         */
        String rules
) {
    public CalculationRequest {
        if (combinationsOrTiles != null && !combinationsOrTiles.isEmpty()) {
            if (playerWind == null || playerWind.isBlank() || vipWind == null || vipWind.isBlank())
                throw new IllegalArgumentException("Ветра обязательны для расчётов");
        }
    }
}
