package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public record RulesSet(boolean canUseOrderedFour,
                       int howManyDoublesForClearSuit,
                       int mahjongScore,
                       int noOrderedDouble,
                       int noOrderedAddScore,
                       Optional<Integer> maximumOneHandScore) implements Rules {
    private final static String DEFAULT_RULES = "classic.rules";

    public static Rules load() {
        return load(DEFAULT_RULES);
    }

    public static Rules load(String resourceName) {
        if (resourceName == null || resourceName.isEmpty())
            resourceName = DEFAULT_RULES;
        Properties props = new Properties();
        try (InputStream input = RulesSet.class.getClassLoader()
                .getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new RuntimeException("Config file not found: " + resourceName);
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }

        return new RulesSet(
                Boolean.parseBoolean(props.getProperty("can.use.ordered.four", "false")),
                Integer.parseInt(props.getProperty("doubles.clear.suit", "3")),
                Integer.parseInt(props.getProperty("mahjong.score", "20")),
                Integer.parseInt(props.getProperty("no.ordered.double", "1")),
                Integer.parseInt(props.getProperty("no.ordered.add.score", "0")),
                props.containsKey("max.one.hand.score")
                        ? Optional.of(Integer.parseInt(props.getProperty("max.one.hand.score")))
                        : Optional.empty()
        );
    }
}
