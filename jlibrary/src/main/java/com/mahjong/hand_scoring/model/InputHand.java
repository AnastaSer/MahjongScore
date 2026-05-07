package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.TilesHelper;

import javax.sound.sampled.Mixer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.mahjong.hand_scoring.model.HandFlags.Flag.*;

/**
 * Класс, получающий переданные от пользователя значения костей и флаги руки
 * */
public class InputHand {
    private List<Combination> combinations;
    private List<Combination> withoutBonuses;
    private List<Combination> bonuses;
    private Map<Tile, Integer> allTiles;
    private Set<Tile.TileType> suits;
    private Set<Integer> winds;
    private Set<Integer> dragons;
    private boolean hasOnesAndNines;
    private boolean hasOthers;
    private Wind playersWind;
    private Wind vipWind;
    private HandFlags knownFlags;
    private boolean isFull;

    public InputHand(Wind playersWind, Wind vipWind,
                     HandFlags knownFlags, List<Combination> combinations) {
        this.combinations = combinations;
        this.playersWind = playersWind;
        this.vipWind = vipWind;
        this.knownFlags = knownFlags;
        verify();
    }

    public InputHand(Wind playersWind, Wind vipWind, List<Combination> combinations) {
        this(playersWind, vipWind, new HandFlags(), combinations);
    }

    public InputHand(List<InputTile> tiles, Wind playersWind, Wind vipWind, HandFlags knownFlags) {
        this(playersWind, vipWind, knownFlags, TilesHelper.tilesToCombinations(tiles));
    }

    public InputHand(List<InputTile> tiles, Wind playersWind, Wind vipWind) {
        this(playersWind, vipWind, TilesHelper.tilesToCombinations(tiles));
    }

    /**
     * Главный метод проверки корректности переданных пользователем данных.
     * Может принимать как полные наборы, так и сокращённые.
     * Проверяет, есть ли совпадающие комбинации,
     * не слишком ли их много (> 13 без учёта бонусов),
     * не слишком ли много костей
     * у передавшего 14, точно ли маджонг,
     * соответствуют ли переданные флаги костям.
     * Если передан полный набор, то выставляет флаги, которые определяются костями.
     * @throws IllegalArgumentException при выявлении любого нарушения
     * */
    private void verify() {
        if (combinations.stream().distinct().count() != combinations.size())
            throw new IllegalArgumentException("Одинаковые комбинации невозможны");
        withoutBonuses = combinations.stream().filter(combo -> !combo.tile().isBonus()).collect(Collectors.toUnmodifiableList());
        if (withoutBonuses.size() > 13)
            throw new IllegalArgumentException("Слишком много комбинаций");
        bonuses = combinations.stream().filter(combo -> combo.tile().isBonus()).collect(Collectors.toUnmodifiableList());
        fillAllTiles();

        int notBonusCount = withoutBonuses.stream().mapToInt(combo ->
                switch (combo.type()) {
                    case SINGLE -> 1;
                    case PAIR -> 2;
                    case THREE, ORDERED_THREE, FOUR, ORDERED_FOUR -> 3;
                }).sum();
        if (notBonusCount > 14)
            throw new IllegalArgumentException("Не бонусных костей без учёта четвёрок не может быть больше 14");
        if (notBonusCount == 14) {
            if (withoutBonuses.size() != 5) {
                throw new IllegalArgumentException("14 костей могут быть только при маджонге");
            } else {
                int countPairs = withoutBonuses.stream().mapToInt( combo ->
                        switch (combo.type()) {
                            case PAIR -> 1;
                            default -> 0;
                        }).sum();
                int countBigOnes = withoutBonuses.stream().mapToInt( combo ->
                        switch (combo.type()) {
                            case THREE, ORDERED_THREE, FOUR, ORDERED_FOUR -> 1;
                            default -> 0;
                        }).sum();
                if (countPairs != 1 || countBigOnes != 4) {
                    throw new IllegalArgumentException("14 костей могут быть только при маджонге");
                } else {
                    knownFlags.addFlag(HandFlags.Flag.MAHJONG);
                }
            }
        }
        isFull = (notBonusCount >= 13);
        if (isFull && notBonusCount == 13 && !knownFlags.hasFlag(HandFlags.Flag.MAHJONG))
                throw new IllegalArgumentException("При маджонге не может быть 13 костей");

        verifyFlags();
        setFlags();
    }

    private void verifyFlags() {
        if (knownFlags.hasFlag(CLEAR_SUIT) && (suits.size() > 1 || !winds.isEmpty() || !dragons.isEmpty()))
            throw new IllegalArgumentException("Чистая масть не подтверждена");
        if (knownFlags.hasFlag(CLEAR_SUIT_WITH_TRUMPS) && (suits.size() > 1))
            throw new IllegalArgumentException("Абсолютно чистая масть не подтверждена");
        if (knownFlags.hasFlag(TRUMPS) && !suits.isEmpty())
            throw new IllegalArgumentException("Есть не только козыри");
        if (knownFlags.hasFlag(TRUMPS_ONES_NINES) && hasOthers)
            throw new IllegalArgumentException("Есть не только козыри, единицы и девятки");
        if (knownFlags.hasFlag(MAHJONG)) {
            if (knownFlags.hasFlag(MIZER) && !isMizer())
                throw new IllegalArgumentException("Есть комбинации, приносящие очки");
            if (knownFlags.hasFlag(NO_ORDEREDS) && !noOrdereds())
                throw new IllegalArgumentException("Есть последовательности");
            if (knownFlags.hasFlag(HAS_ALL_DRAGONS) && !hasAllDragons())
                throw new IllegalArgumentException("Либо не все драконы, либо пара не из драконов");
            if (knownFlags.hasFlag(HAS_ALL_WINDS) && !hasAllWinds())
                throw new IllegalArgumentException("Либо не все ветра, либо пара не из ветров");
        }
    }

    private void setFlags() {
        if(!isFull)
            return;

        if (suits.isEmpty())
            knownFlags.addFlag(TRUMPS);
        if (hasOnesAndNines && !hasOthers)
            knownFlags.addFlag(TRUMPS_ONES_NINES);
        if (suits.size() == 1) {
            if (winds.isEmpty() && dragons.isEmpty())
                knownFlags.addFlag(CLEAR_SUIT);
            else
                knownFlags.addFlag(CLEAR_SUIT_WITH_TRUMPS);
        }
        if (knownFlags.hasFlag(MAHJONG)) {
            if(isMizer())
                knownFlags.addFlag(MIZER);
            if (noOrdereds())
                knownFlags.addFlag(NO_ORDEREDS);
            if (hasAllDragons())
                knownFlags.addFlag(HAS_ALL_DRAGONS);
            if (hasAllWinds())
                knownFlags.addFlag(HAS_ALL_WINDS);
        }
    }

    private boolean isMizer() {
        return combinations.stream().filter(combo -> switch (combo.type()) {
            case FOUR, THREE -> true;
            case PAIR -> {
                if (combo.tile().isDragon())
                    yield true;
                yield combo.tile().isSameWind(playersWind) || combo.tile().isSameWind(vipWind);
            }
            default -> false;
        }).count() == 0;
    }

    private boolean noOrdereds() {
        return combinations.stream().filter(combo -> switch (combo.type()) {
            case ORDERED_FOUR, ORDERED_THREE -> true;
            default -> false;
        }).count() == 0;
    }

    private boolean hasAllWinds() {
        return winds.size() == 4 && combinations.stream().
                filter(combo -> combo.type().equals(Combination.CombinationType.PAIR) && combo.tile().type().equals(Tile.TileType.WIND))
                .count() == 1;
    }

    private boolean hasAllDragons() {
        return dragons.size() == 3 && combinations.stream().
                filter(combo -> combo.type().equals(Combination.CombinationType.PAIR) && combo.tile().type().equals(Tile.TileType.DRAGON))
                .count() == 1;
    }

    private void fillAllTiles() {
        allTiles = new HashMap();
        suits = new HashSet<>();
        winds = new HashSet<>();
        dragons = new HashSet<>();
        hasOnesAndNines = false;
        hasOthers = false;
        for (Combination combo: withoutBonuses) {
            switch (combo.type()) {
                case SINGLE -> addTile(combo.tile());
                case PAIR -> addTiles(combo.tile(), 2);
                case THREE -> addTiles(combo.tile(), 3);
                case FOUR -> addTiles(combo.tile(), 4);
                case ORDERED_THREE -> addOrderedTiles(combo.tile(), true);
                case ORDERED_FOUR -> addOrderedTiles(combo.tile(), false);
            }
        }
    }

    private void addTile(Tile tile) {
        addTiles(tile, 1);
    }

    private void addTiles(Tile tile, int count) {
        if (!allTiles.containsKey(tile)) {
            allTiles.put(tile, 0);
        }
        int newValue = allTiles.get(tile) + count;
        if (newValue > 4)
            throw new IllegalArgumentException("Каждой кости в игре по 4");
        allTiles.put(tile, allTiles.get(tile) + 1);
        if (tile.isSuit()) {
            suits.add(tile.type());
            if (tile.number() == 1 || tile.number() == 9)
                hasOnesAndNines = true;
            else
                hasOthers = true;
        } else if (tile.type() == Tile.TileType.WIND) {
            winds.add(tile.number());
        } else {
            dragons.add(tile.number());
        }
    }

    private void addOrderedTiles(Tile tile, boolean isClassicOrder) {
        addTile(tile);
        Tile second = Tile.next(tile);
        addTile(second);
        Tile third = Tile.next(second);
        addTile(third);
        if (!isClassicOrder) {
            addTile(Tile.next(third));
        }
    }
}
