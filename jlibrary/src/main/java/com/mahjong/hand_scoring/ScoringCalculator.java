package com.mahjong.hand_scoring;

import com.mahjong.hand_scoring.model.Rules;

/**
 * Основной класс библиотеки расчёта очков
 * */
public class ScoringCalculator {
    private static Rules activeRules;

    public static Rules getActiveRules() {
        return activeRules;
    }
}
