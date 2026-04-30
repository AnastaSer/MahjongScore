package com.mahjong.hand_scoring.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

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
        List<String> correctInputs = List.of("знак 8", "символ 9",
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
        Tile wind1 = new Tile(Tile.TileType.WIND, 1);
        List<Tile> sameToWind1 = List.of(Tile.of("ветер 1"),
                Tile.of("ветер восточный"),
                Tile.of("wind 1"),
                Tile.of("wind east"));
        sameToWind1.stream().forEach(same -> assertEquals(same, wind1));

        Tile wind2 = new Tile(Tile.TileType.WIND, 2);
        List<Tile> sameToWind2 = List.of(
                Tile.of("ветер южный"),
                Tile.of("wind south"));
        sameToWind2.stream().forEach(same -> assertEquals(same, wind2));

        Tile wind3 = new Tile(Tile.TileType.WIND, 3);
        List<Tile> sameToWind3 = List.of(
                Tile.of("ветер западный"),
                Tile.of("wind west"));
        sameToWind3.stream().forEach(same -> assertEquals(same, wind3));

        Tile wind4 = new Tile(Tile.TileType.WIND, 4);
        List<Tile> sameToWind4 = List.of(
                Tile.of("ветер северный"),
                Tile.of("wind north"));
        sameToWind4.stream().forEach(same -> assertEquals(same, wind4));

        Tile dragon1 = new Tile(Tile.TileType.DRAGON, 1);
        List<Tile> sameToDragon1 = List.of(
                Tile.of("дракон белый"),
                Tile.of("dragon white"));
        sameToDragon1.stream().forEach(same -> assertEquals(same, dragon1));

        Tile dragon2 = new Tile(Tile.TileType.DRAGON, 2);
        List<Tile> sameToDragon2 = List.of(
                Tile.of("дракон красный"),
                Tile.of("dragon red"));
        sameToDragon2.stream().forEach(same -> assertEquals(same, dragon2));

        Tile dragon3 = new Tile(Tile.TileType.DRAGON, 3);
        List<Tile> sameToDragon3 = List.of(
                Tile.of("дракон зелёный"),
                Tile.of("dragon green"));
        sameToDragon3.stream().forEach(same -> assertEquals(same, dragon3));
    }
}
