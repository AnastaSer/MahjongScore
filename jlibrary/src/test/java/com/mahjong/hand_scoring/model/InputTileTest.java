package com.mahjong.hand_scoring.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.mahjong.hand_scoring.model.Tile.TileType.SIGN;

public class InputTileTest {
    private final static Tile tileToCompare = new Tile(SIGN, 1);

    private Optional<InputTile> createFrom(String describe, Optional<Tile> tile) {
        try {
            if (tile.isEmpty())
                return Optional.of(InputTile.of(describe));
            else
                return Optional.of(InputTile.of(describe, tile.get()));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Test
    public void incorrectTest() {
        assert(createFrom(null, Optional.empty()).isEmpty());
        assert(createFrom(null, Optional.of(tileToCompare)).isEmpty());
        List<String> incorrectInput = List.of("", "open", "close");
        incorrectInput.stream().forEach(s-> {
            System.out.println("Тестирую: '" + s + "'");
            assert(createFrom(s, Optional.empty()).isEmpty());
            assert(createFrom(s, Optional.of(tileToCompare)).isEmpty());
        });
    }

    @Test
    public void correctTest() {
        List<String> correctSeparators = List.of(";", "|", ",", "separator", "разделитель");
        correctSeparators.stream().forEach(s -> {
            System.out.println("Тестирую: '" + s + "'");
            assert(!createFrom(s, Optional.empty()).isEmpty());
        });

        List<String> correctInput = List.of("opened", "открытая", "открытый", "closed", "закрытая", "закрытый");
        correctInput.stream().forEach(s-> {
            System.out.println("Тестирую: '" + s + "'");
            assert(!createFrom(s, Optional.of(tileToCompare)).isEmpty());
            String buf = s + " " + tileToCompare.ruStr();
            System.out.println("Тестирую: '" + buf + "'");
            assert(!createFrom(buf, Optional.empty()).isEmpty());
        });
    }

    @Test
    public void equalTest() {
        testEquals(true, List.of("opened", "открытая", "открытый"));
        testEquals(false, List.of("closed", "закрытая", "закрытый"));
    }

    private void testEquals(boolean isOpen, List<String> sameNames) {
        InputTile toCompare = new InputTile(InputTile.InputTileType.TILE, isOpen, tileToCompare);
        sameNames.stream().forEach(s -> {
            assert(createFrom(s, Optional.of(tileToCompare)).get().equals(toCompare));
        });
    }
}
