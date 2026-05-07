package com.mahjong.hand_scoring.model;

import org.junit.jupiter.api.Test;
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TileTest {
    private Optional<Tile> createFrom(String describe) {
        try {
            return Optional.of(Tile.of(describe));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Test
    public void incorrectTiles() {
        assertThrows(IllegalArgumentException.class, () -> Tile.of(null));

        List<String> incorrectInputs = List.of("",
                "шестёрка ветров",
                "ветер шесть",
                "dragon 4",
                "bamboo 11",
                "sign -11",
                "бонусный ветер 2",
                "бонусный сезон 5",
                "ветер жёлтый",
                "дракон восточный");

        incorrectInputs.stream().forEach(s-> {
            System.out.println("Тестирую: '" + s + "'");
            assert(createFrom(s).isEmpty());
        });
    }

    @Test
    public void correctTiles(){
        List<String> correctInputs = List.of("знак 8",
                "символ 9",
                "дот 2",
                "точка 6",
                "бамбук 7",
                "ветер 3",
                "дракон 1",
                "бонусный сезон 3",
                "бонусный цветок 2",
                "sign 3",
                "dot 8",
                "bamboo 2",
                "wind 3",
                "dragon 1",
                "bonus season 1",
                "bonus flower 4",
                "wind north",
                "dragon red");

        correctInputs.stream().forEach(s-> {
            System.out.println("Тестирую: '" + s + "'");
            assert(!createFrom(s).isEmpty());
        });
    }

    @Test
    public void equalTiles() {
        testEqualWind(1, List.of("east", "восточный"));
        testEqualWind(2, List.of("south", "южный"));
        testEqualWind(3, List.of("west", "западный"));
        testEqualWind(4, List.of("north", "северный"));

        testEqualDragon(1, List.of("white", "белый"));
        testEqualDragon(2, List.of("red", "красный"));
        testEqualDragon(3, List.of("green", "зелёный"));

        testEqualBonus(Tile.TileType.BONUS_FLOWER, List.of("flower", "цветок"));
        testEqualBonus(Tile.TileType.BONUS_SEASON, List.of("season", "сезон"));

        testEqualSuit(Tile.TileType.SIGN, List.of("sign", "знак", "символ"));
        testEqualSuit(Tile.TileType.DOT, List.of("dot", "дот", "точка"));
        testEqualSuit(Tile.TileType.BAMBOO, List.of("bamboo", "бамбук"));
    }

    private void testEqual(Tile origin, List<Tile> same) {
        System.out.println("Тестирую: " + origin);
        same.stream().forEach(copy -> {
            System.out.println("Сравниваю с : " + copy);
            assertEquals(copy, origin);
        });
    }

    private void testEqualNotSuit(Tile.TileType type, List<String> prefixes, int number, List<String> someNames) {
        List<String> names = new ArrayList<>();
        names.add("" + number);
        names.addAll(someNames);
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            int index = i;
            tiles.addAll(names.stream().
                    map(name -> Tile.of(prefixes.get(index) + " " + name)).
                    collect(Collectors.toList()));
        }
        testEqual(new Tile(type, number), tiles);
    }

    private void testEqualWind(int number, List<String> someNames) {
        testEqualNotSuit(Tile.TileType.WIND, List.of("wind", "ветер"), number, someNames);
    }

    private void testEqualDragon(int number, List<String> someNames) {
        testEqualNotSuit(Tile.TileType.DRAGON, List.of("dragon", "дракон"), number, someNames);
    }

    private void testEqualBonus(Tile.TileType type, List<String> names) {
        List<String> prefixes = List.of("bonus", "бонусный", "бонус");
        for (int n = 1; n <= 4; n++) {
            int number = n;
            List<Tile> tiles = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                int index = i;
                tiles.addAll(names.stream().
                        map(name -> Tile.of(prefixes.get(index) + " " + name + " " + number)).
                        collect(Collectors.toList()));
            }
            testEqual(new Tile(type, number), tiles);
        }
    }

    private void testEqualSuit(Tile.TileType type, List<String> names) {
        for (int n = 1; n <= 9; n++) {
            int number = n;
            testEqual(new Tile(type, number),
                    names.stream().map(name -> Tile.of(name + " " + number)).
                            collect(Collectors.toUnmodifiableList()));
        }
    }
}
