package com.mahjong.hand_scoring.model;

import com.mahjong.hand_scoring.utils.StringHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public record RulesSet(boolean canUseOrderedFour,
                       int howManyDoublesForClearSuit,
                       int mahjongScore,
                       int noOrderedDouble,
                       int noOrderedAddScore,
                       Optional<Integer> maximumOneHandScore,
                       String name) implements Rules {
    private final static String DEFAULT_RULES = "classic.rules";
    private final static Map<String, String> rulesMap = Map.of(
            "классика", "classic.rules",
            "друзья", "friends.rules",
            "родители", "parents.rules");
    private final static Map<String, String> rulesOutputStrMap = Map.of(
            "classic.rules", "из сети",
            "friends.rules", "Светы и компании",
            "parents.rules", "родитетелей");

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
                        : Optional.empty(),
                resourceName
        );
    }

    /**
     * Метод, проверяющий, можно ли по переданному слову сгенерировать вариант правил
     * */
    public static boolean isRulesVariant(String lastArg) {
        return rulesMap.containsKey(StringHelper.normalize(lastArg));
    }

    /**
     * Метод-фабрика, возвращающий вариант правил по словесному описанию
     * */
    public static Rules of(String lastArg) {
        System.out.println("Create from: " + lastArg + " file: " + rulesMap.get(StringHelper.normalize(lastArg)));
        return load(rulesMap.get(StringHelper.normalize(lastArg)));
    }

    /**
     * Метод, возвращающий описание правил на русском языке
     * */
    public static String outputName(Rules rules) {
        return rulesOutputStrMap.get(rules.name());
    }
}
