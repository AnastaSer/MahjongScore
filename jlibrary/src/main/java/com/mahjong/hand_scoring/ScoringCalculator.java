package com.mahjong.hand_scoring;

import com.mahjong.hand_scoring.model.CompleteHand;
import com.mahjong.hand_scoring.model.Rules;

import java.util.Arrays;

/**
 * Основной класс библиотеки расчёта очков
 * */
public class ScoringCalculator {
    private static Rules activeRules = new Rules();
    private final static String HELP = "Использование: java -jar hand_scoring.jar ветер игрока ветер преимущественный [собранные кости, с разделением комбинаций через \' ; \']  [перечень флагов через\' + \']";

    public static Rules getActiveRules() {
        return activeRules;
    }

    public static void main(String[] args) {
        Arrays.stream(args).forEach(System.out::println);
        CompleteHand completeHand = CompleteHand.of(args);
        System.out.println("Очки за комбинацию: " + completeHand.getScore());
        /*
        System.out.println();
        String inputLine = args[0];
        HandInput hand = HandInputParser.parse(inputLine);
        ScoreResult result = new ScoreCalculator(new RiichiBasicRuleSet()).calculate(hand);

        System.out.println("Fan: " + result.fan());
        System.out.println("Points: " + result.points());
        if (!result.details().isEmpty()) {
            System.out.println("Details: " + result.details());
        }*/
    }
}
