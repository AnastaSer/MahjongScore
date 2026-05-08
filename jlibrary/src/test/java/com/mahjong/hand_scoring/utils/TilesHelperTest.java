package com.mahjong.hand_scoring.utils;

import com.mahjong.hand_scoring.model.Combination;
import com.mahjong.hand_scoring.model.InputTile;
import com.mahjong.hand_scoring.model.Tile;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mahjong.hand_scoring.model.Tile.TileType.*;
import static org.assertj.core.api.Assertions.*;

public class TilesHelperTest {
    private static final Tile sign1 = new Tile(SIGN, 1);
    private static final Tile sign2 = new Tile(SIGN, 2);
    private static final Tile sign3 = new Tile(SIGN, 3);
    private static final Tile sign4 = new Tile(SIGN, 4);
    private static final Tile bonusF = new Tile(BONUS_FLOWER, 1);
    private static final Tile dragon1 = new Tile(DRAGON, 1);
    private static final Tile wind2 = new Tile(WIND, 2);
    private static final Combination closedOneSign1 = Combination.of("closed single sign 1");
    private static final Combination closedOneSign2 = Combination.of("closed single sign 2");
    private static final Combination closedOneSign3 = Combination.of("closed single sign 3");
    private static final Combination closedOneSign4 = Combination.of("closed single sign 4");
    private static final Combination closedOrderedThreeSign1 = Combination.of("closed ordered three sign 1");
    private static final Combination openedThreeSign3 = Combination.of("opened three sign 3");
    private static final Combination closedThreeSign3 = Combination.of("closed three sign 3");
    private static final Combination closedFourSign3 = Combination.of("closed four sign 3");
    private static final Combination closedPairSign3 = Combination.of("closed pair sign 3");
    private static final Combination bonus = Combination.of("opened single bonus flower 1");
    private static final Combination closedDragon1 = Combination.of("closed single dragon 1");
    private static final Combination closedWind2 = Combination.of("closed single wind 2");

    @Test
    public void correctTests() {
        testCorrect(List.of(closedOneSign1),
                List.of(InputTile.of(false, sign1)));
        testCorrect(List.of(closedOneSign1, closedOneSign2),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2)));
        testCorrect(List.of(closedOrderedThreeSign1),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2), InputTile.of(false, sign3)));
        testCorrect(List.of(closedOrderedThreeSign1, closedOneSign3),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2),
                        InputTile.of(false, sign3), InputTile.of(false, sign3)));
        testCorrect(List.of(closedOrderedThreeSign1, closedPairSign3),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2),
                        InputTile.of(false, sign3), InputTile.of(false, sign3), InputTile.of(false, sign3)));
        testCorrect(List.of(closedOrderedThreeSign1, closedThreeSign3),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2),
                        InputTile.of(false, sign3), InputTile.of(false, sign3), InputTile.of(false, sign3), InputTile.of(false, sign3)));
        testCorrect(List.of(closedOneSign1, closedOneSign2, openedThreeSign3),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2),
                        InputTile.of(true, sign3), InputTile.of(true, sign3), InputTile.of(true, sign3)));
        testCorrect(List.of(closedOneSign1, closedOneSign2, closedThreeSign3),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2),
                        InputTile.separator(),
                        InputTile.of(false, sign3), InputTile.of(false, sign3), InputTile.of(false, sign3)));
        testCorrect(List.of(closedOneSign1, closedOneSign2, closedThreeSign3, bonus),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2),
                        InputTile.of(true, bonusF),
                        InputTile.of(false, sign3), InputTile.of(false, sign3), InputTile.of(false, sign3)));
        testCorrect(List.of(closedOneSign1, closedOneSign2, closedThreeSign3, closedDragon1),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2),
                        InputTile.of(false, dragon1),
                        InputTile.of(false, sign3), InputTile.of(false, sign3), InputTile.of(false, sign3)));
        testCorrect(List.of(closedOneSign1, closedOneSign2, closedThreeSign3, closedWind2),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2),
                        InputTile.of(false, wind2),
                        InputTile.of(false, sign3), InputTile.of(false, sign3), InputTile.of(false, sign3)));
        testCorrect(List.of(closedOneSign1, closedOneSign2, closedFourSign3, closedOneSign4),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2),
                        InputTile.of(false, sign4),
                        InputTile.of(false, sign3), InputTile.of(false, sign3), InputTile.of(false, sign3), InputTile.of(false, sign3)));
        testCorrect(List.of(closedOrderedThreeSign1, closedOneSign1),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2), InputTile.of(false, sign3),
                        InputTile.of(false, sign1)));
        testCorrect(List.of(closedOrderedThreeSign1, closedOrderedThreeSign1),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2), InputTile.of(false, sign3),
                        InputTile.of(false, sign1), InputTile.of(false, sign2), InputTile.of(false, sign3)));
        testCorrect(List.of(closedOrderedThreeSign1, closedOrderedThreeSign1, closedOrderedThreeSign1),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2), InputTile.of(false, sign3),
                        InputTile.of(false, sign1), InputTile.of(false, sign2), InputTile.of(false, sign3),
                        InputTile.of(false, sign1), InputTile.of(false, sign2), InputTile.of(false, sign3)));

        testCorrect(List.of(closedOrderedThreeSign1, closedOrderedThreeSign1, closedOrderedThreeSign1, closedOrderedThreeSign1),
                List.of(InputTile.of(false, sign1), InputTile.of(false, sign2), InputTile.of(false, sign3),
                        InputTile.of(false, sign1), InputTile.of(false, sign2), InputTile.of(false, sign3),
                        InputTile.of(false, sign1), InputTile.of(false, sign2), InputTile.of(false, sign3),
                        InputTile.of(false, sign1), InputTile.of(false, sign2), InputTile.of(false, sign3)));
    }

    @Test
    public void incorrectTests() {
        testIncorrect(List.of(InputTile.of(false, sign1), InputTile.separator(), InputTile.of(false, sign1)));
        testIncorrect(List.of(InputTile.of(false, sign1), InputTile.separator(), InputTile.of(false, sign1)));
    }

    private void testCorrect(List<Combination> expected, List<InputTile> input) {
        System.out.println("Тестирую набор: " + input);
        System.out.println("Ожидаю:  " + expected);
        List<Combination> real = TilesHelper.tilesToCombinations(input);
        System.out.println("Получаю: " + real);
        assertThat(real).containsExactlyInAnyOrderElementsOf(expected);
    }

    private void testIncorrect(List<InputTile> input) {
        System.out.println("Тестирую набор: " + input);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> TilesHelper.tilesToCombinations(input))
                .withMessage("В игре не может быть одинаковых комбинаций");
    }
}
