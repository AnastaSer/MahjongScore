package com.mahjong.domain.dto;

/**
 * Ответ сервера с результатом расчёта.
 */
public record CalculationResponse(
        int score,
        boolean isMahjong) {}
