package com.mahjong.hand_scoring;

import com.mahjong.hand_scoring.model.CompleteHand;
import com.mahjong.hand_scoring.model.Rules;
import com.mahjong.hand_scoring.model.RulesSet;
import com.mahjong.hand_scoring.utils.StringHelper;

import java.util.Arrays;

/**
 * Основной класс библиотеки расчёта очков
 * */
public class ScoringCalculator {
    private static Rules activeRules = RulesSet.load();
    private final static String HELP = "Использование: java -jar hand_scoring.jar ветер игрока ветер преимущественный [собранные кости, с разделением комбинаций через \' ; \']  [перечень флагов через\' + \']";

    public static Rules getActiveRules() {
        return activeRules;
    }

    public static void main(String[] args) {
        if (args.length >= 1 && StringHelper.normalize(args[0]).equals("help")) {
            System.out.println(HELP);
            return;
        }
        try {
            System.out.println("Классические правила");
            CompleteHand completeHand = CompleteHand.of(args);
            System.out.println("Очки за комбинацию: " + completeHand.getScore());
            System.out.println("Правила с увеличенным лимитом");
            ScoringCalculator.activeRules = RulesSet.load("test.rules");
            completeHand = CompleteHand.of(args);
            System.out.println("Очки за комбинацию: " + completeHand.getScore());
            System.out.println("Правила Светы");
            ScoringCalculator.activeRules = RulesSet.load("friends.rules");
            completeHand = CompleteHand.of(args);
            System.out.println("Очки за комбинацию: " + completeHand.getScore());
            System.out.println("Правила родителей");
            ScoringCalculator.activeRules = RulesSet.load("parents.rules");
            completeHand = CompleteHand.of(args);
            System.out.println("Очки за комбинацию: " + completeHand.getScore());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
