package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс для полей, хранящих флаги собранной игроком руки
 * */
public class HandFlags {
    private final static Logger log = LoggerFactory.getLogger(HandFlags.class);

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
        public static Flag of(String description) {
            return switch (StringHelper.normalize(description)) {
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
                default -> throw new IllegalArgumentException("Введите точное описание случая: \'" + description + "\'");
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
    private Set<Flag> flagsSet = new TreeSet<>();

    /**
     * Конструктор из списка флагов
     * @throws IllegalArgumentException, если введённые значения пусты, некорректны или противоречат логике игры
     * */
    public HandFlags(List<Flag> flags) {
        if (flags == null)
            throw new IllegalArgumentException("Введите список флагов или воспользуйтесь пустым конструктором");
        if (!flags.isEmpty()) {
            flags.forEach(this::addUnchekedFlag);
            verify();
            flagsSet.addAll(flags);
        }
    }

    /**
     * Конструктор из массива флагов
     * @throws IllegalArgumentException, если введённые значения пусты, некорректны или противоречат логике игры
     * */
    public HandFlags(Flag ... flags) {
        if (flags == null)
            throw new IllegalArgumentException("Введите список флагов или воспользуйтесь пустым конструктором");
        if (flags.length != 0) {
            Arrays.stream(flags).forEach(this::addUnchekedFlag);
            verify();
            flagsSet.addAll(Arrays.stream(flags).collect(Collectors.toSet()));
        }
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
        flagsSet.add(flag);
    }

    /**
     * Конструктор из другого поля с флагами
     * @throws IllegalArgumentException, если передан null
     * */
    public HandFlags(HandFlags toCopy) {
        if (toCopy == null)
            throw new IllegalArgumentException("Нельзя создавать копию null");
        this.allFlags = toCopy.allFlags;
        this.flagsSet.addAll(toCopy.flagsSet);
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

    private boolean hasBothFlags(Flag flag1, Flag flag2) {
        return hasFlag(flag1) && hasFlag(flag2);
    }

    /**
     * Метод-обёртка для всех публичных методов.
     * Проводит проверку логики заполнения флагов после применения.
     * Отменяет действие, если после его применения ломается логика заполнения флагов.
     * */
    private void applyMethod(List<Flag> flags, Consumer<Flag> method) {
        HandFlags was = new HandFlags(this);
        flags.stream().forEach(method);
        try {
            verify();
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка обработки флагов: {}", e.getMessage());
            copyFrom(was);
        }
    }

    private void applyMethod(Flag flag, Consumer<Flag> method) {
        HandFlags was = new HandFlags(this);
        method.accept(flag);
        try {
            verify();
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка обработки флагов: {}", e.getMessage());
            copyFrom(was);
        }
    }

    private void copyFrom(HandFlags was) {
        this.allFlags = was.allFlags;
        this.flagsSet.clear();
        this.flagsSet.addAll(was.flagsSet);
    }

    private void addUnchekedFlag(Flag flag) {
        allFlags |= flag.getValue();
        flagsSet.add(flag);
    }

    private void removeUnchekedFlag(Flag flag) {
        allFlags &= ~flag.getValue();
        if (flagsSet.contains(flag))
            flagsSet.remove(flag);
    }

    /**
     * Метод, проверяющий логику сочетаемости флагов
     * */
    private void verify() {
        if (hasBothFlags(Flag.CLEAR_SUIT, Flag.TRUMPS)) {
            throw new IllegalArgumentException("Либо абсолютно чистая масть, либо только козыри");
        }
        if (hasBothFlags(Flag.CLEAR_SUIT, Flag.CLEAR_SUIT_WITH_TRUMPS)) {
            throw new IllegalArgumentException("Либо абсолютно чистая масть, либо чистая масть с козырями");
        }
        if (hasBothFlags(Flag.CLEAR_SUIT_WITH_TRUMPS, Flag.TRUMPS)) {
            throw new IllegalArgumentException("Либо чистая масть с козырями, либо только козыри");
        }
        if (hasBothFlags(Flag.TRUMPS, Flag.TRUMPS_ONES_NINES)) {
            throw new IllegalArgumentException("Либо только козыри, либо козыри с единицами и девятками");
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
    }

    @Override
    public String toString() {
        return Long.toBinaryString(allFlags);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HandFlags handFlags = (HandFlags) o;
        return allFlags == handFlags.allFlags;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(allFlags);
    }
}
