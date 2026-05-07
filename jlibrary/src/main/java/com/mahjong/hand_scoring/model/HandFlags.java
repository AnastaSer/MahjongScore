package com.mahjong.hand_scoring.model;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Класс для полей, хранящих флаги собранной игроком руки
 * */
public class HandFlags {
    public enum Flag {
        CLEAR_SUIT(0x1),
        CLEAR_SUIT_WITH_TRUMPS(0x2),
        TRUMPS(0x4),
        TRUMPS_ONES_NINES(0x8),

        MAHJONG(0x10),
        MIZER(0x20),
        NO_ORDEREDS(0x40),
        HAS_ALL_WINDS(0x80),
        HAS_ALL_DRAGONS(0x100),
        WAS_WAITING_FROM_THE_START(0x200),
        FINISHED_WITH_ONE_POSSIBLE(0x400),
        FINISHED_FROM_THE_WALL(0x800),
        FINISHED_WITH_FREE_TILE(0x1000),
        FINISHED_WITH_LAST_IN_GAME(0x2000),
        FINISHED_BY_ROBBING_OPEN_KONG(0x4000);

        private long value;

        Flag(long value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }

        /**
         * Метод-фабрика для создания флага руки по словесному обозначению
         * @throws IllegalArgumentException, если введённые значения некорректны
         * */
        public Flag of(String description) {
            return switch (description) {
                case "чистая масть" -> CLEAR_SUIT;
                case "чистая масть с драконами и ветрами" -> CLEAR_SUIT_WITH_TRUMPS;
                case "только драконы и ветра" -> TRUMPS;
                case "драконы, ветра, единицы и девятки" -> TRUMPS_ONES_NINES;
                case "маджонг" -> MAHJONG;
                case "мизер" -> MIZER;
                case "без последовательностей" -> NO_ORDEREDS;
                case "все драконы" -> HAS_ALL_DRAGONS;
                case "все ветра" -> HAS_ALL_WINDS;
                case "предмаджонг со старта" ->  WAS_WAITING_FROM_THE_START;
                case "завершение единственной возможной" -> FINISHED_WITH_ONE_POSSIBLE;
                case "завершение костью со стены" -> FINISHED_FROM_THE_WALL;
                case "завершение свободной костью" -> FINISHED_WITH_FREE_TILE;
                case "завершение последней доступной" -> FINISHED_WITH_LAST_IN_GAME;
                case "завершение ограблением открытого конга" -> FINISHED_BY_ROBBING_OPEN_KONG;
                default -> throw new IllegalArgumentException("Введите точное описание");
            };
        }
    }

    private static final long onlyMahjongFlags;
    static {
        onlyMahjongFlags = Stream.of(Flag.MIZER,
                Flag.NO_ORDEREDS,
                Flag.HAS_ALL_WINDS,
                Flag.HAS_ALL_DRAGONS,
                Flag.WAS_WAITING_FROM_THE_START,
                Flag.FINISHED_WITH_ONE_POSSIBLE,
                Flag.FINISHED_FROM_THE_WALL,
                Flag.FINISHED_WITH_FREE_TILE,
                Flag.FINISHED_WITH_LAST_IN_GAME,
                Flag.FINISHED_BY_ROBBING_OPEN_KONG).
                mapToLong(f -> f.getValue()).reduce(0l, (a, b) -> a | b);
    }

    private long allFlags = 0l;

    /**
     * Конструктор из списка флагов
     * @throws IllegalArgumentException, если введённые значения пусты, некорректны или противоречат логике игры
     * */
    public HandFlags(List<Flag> flags) {
        if (flags == null)
            throw new IllegalArgumentException("Введите список флагов или воспользуйтесь пустым конструктором");
        flags.forEach(this::addUnchekedFlag);
        verify();
    }

    /**
     * Конструктор из массива флагов
     * @throws IllegalArgumentException, если введённые значения пусты, некорректны или противоречат логике игры
     * */
    public HandFlags(Flag ... flags) {
        if (flags == null)
            throw new IllegalArgumentException("Введите список флагов или воспользуйтесь пустым конструктором");
        Arrays.stream(flags).forEach(this::addUnchekedFlag);
        verify();
    }

    /**
     * Конструктор из одного флага
     * @throws IllegalArgumentException, если введённые значения пусты, некорректны или противоречат логике игры
     * */
    public HandFlags(Flag flag) {
        if (flag == null)
            throw new IllegalArgumentException("Введите флаг или воспользуйтесь пустым конструктором");
        addUnchekedFlag(flag);
        verify();
    }

    public HandFlags() {}

    public void addFlags(List<Flag> flags) {
        applyMethod(flags, this::addUnchekedFlag);
    }

    public void addFlag(Flag flag) {
        applyMethod(flag, this::addUnchekedFlag);
    }

    public void removeFlags(List<Flag> flags) {
        applyMethod(flags, this::removeUnchekedFlag);
    }

    public void removeFlag(Flag flag) {
        applyMethod(flag, this::removeUnchekedFlag);
    }

    public boolean hasFlag(Flag flag) {
        return (allFlags & flag.getValue()) == flag.getValue();
    }

    public boolean hasAnyOfFlags(Flag ... flags) {
        for (Flag flag: flags) {
            if (hasFlag(flag)) return true;
        }
        return false;
    }

    private boolean hasAnyTwoOfFlags(Flag ... flags) {
        int count = 0;
        for (Flag flag: flags) {
            if (hasFlag(flag)) count++;
        }
        return count > 1;
    }

    /**
     * Метод-обёртка для всех публичных методов.
     * Проводит проверку логики заполнения флагов после применения.
     * Отменяет действие, если после его применения ломается логика заполнения флагов.
     * */
    private void applyMethod(List<Flag> flags, Consumer<Flag> method) {
        long wasFlags = allFlags;
        flags.stream().forEach(method);
        try {
            verify();
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка обработки флагов:" + e.getMessage());
            allFlags = wasFlags;
        }
    }

    private void applyMethod(Flag flag, Consumer<Flag> method) {
        long wasFlags = allFlags;
        method.accept(flag);
        try {
            verify();
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка обработки флага:" + e.getMessage());
            allFlags = wasFlags;
        }
    }

    private void addUnchekedFlag(Flag flag) {
        allFlags |= flag.getValue();
    }

    private void removeUnchekedFlag(Flag flag) {
        allFlags &= ~flag.getValue();
    }

    /**
     * Метод, проверяющий логику сочетаемости флагов
     * */
    private void verify() {
        if (hasAnyTwoOfFlags(Flag.CLEAR_SUIT, Flag.CLEAR_SUIT_WITH_TRUMPS, Flag.TRUMPS, Flag.TRUMPS_ONES_NINES)) {
            throw new IllegalArgumentException("Может быть либо чистая масть, либо чистая масть с козырями, либо только козыри, либо козыри с единицами и девятками");
        }
        if (!hasFlag(Flag.MAHJONG) && ((allFlags & onlyMahjongFlags) != 0)) {
            throw new IllegalArgumentException("Флаги, выбранные для маджонга не применимы без маджонга");
        }
        if (hasFlag(Flag.MIZER) && hasAnyOfFlags(
                Flag.NO_ORDEREDS, Flag.HAS_ALL_DRAGONS, Flag.HAS_ALL_WINDS,
                Flag.TRUMPS, Flag.TRUMPS_ONES_NINES)) {
            throw new IllegalArgumentException("Мизер не может быть с чистыми козырями");
        }
        if (hasFlag(Flag.HAS_ALL_DRAGONS) && hasFlag(Flag.HAS_ALL_WINDS))
            throw new IllegalArgumentException("Невозможно собрать и драконов, и ветра для умножения маджонга");
        System.out.println("Ok");
    }
}
