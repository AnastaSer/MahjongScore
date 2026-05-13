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
    private final static String HELP = "Использование: java -jar hand_scoring.jar ветер игрока ветер преимущественный [собранные кости, с разделением комбинаций через \' ; \']  [перечень флагов через\' + \'] [правила:{классика/друзья/родители}]";
    private final static String RULES_PREFIX = "правила:";

    public static void main(String[] args) {
        if (args.length >= 1 && StringHelper.normalize(args[0]).equals("help")) {
            System.out.println(HELP);
            return;
        }
        boolean rulesAreSet = args.length >= 1 && args[args.length - 1].startsWith(RULES_PREFIX);
        Rules activeRules = (rulesAreSet && RulesSet.isRulesVariant(args[args.length - 1].substring(RULES_PREFIX.length()))) ?
            RulesSet.of(args[args.length - 1].substring(RULES_PREFIX.length())) : RulesSet.load();
        if (rulesAreSet)
            args[args.length - 1] = "";
        try {
            CompleteHand completeHand = CompleteHand.of(args, activeRules);
            System.out.println("Очки посчитаны по правилам " + RulesSet.outputName(activeRules));
            System.out.println("Очки за комбинацию: " + completeHand.getScore());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
