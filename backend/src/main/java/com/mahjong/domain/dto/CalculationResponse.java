package com.mahjong.domain.dto;

import java.util.List;

/**
 * Ответ сервера с результатом расчёта.
 */
public record CalculationResponse(
        int score,
        boolean isMahjong,
        List<String> appliedFlags) {}
