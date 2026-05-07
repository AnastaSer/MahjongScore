package com.mahjong.hand_scoring.model;

/**
 * Класс для хранения актуальных для игры правил.
 * */
public class Rules {
    /**
     * Метод возвращает признак, можно ли собирать более 3х фишек в последовательность
     * */
    public boolean canUseOrderedFour() {
        return false;
    }
}
