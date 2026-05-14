package com.mahjong.domain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahjong.domain.dto.CalculationRequest;
import com.mahjong.domain.dto.CalculationResponse;
import com.mahjong.hand_scoring.model.CompleteHand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер, отвечающий за обработку входящих запросов 
 * */
@RestController
@RequestMapping("/api/mahjong_scores")
public class ScoreController {
    private final static Logger log = LoggerFactory.getLogger(ScoreController.class);

    /**
     * Обработка запроса на расчёт одной руки
     * */
    @PostMapping("/calculate_one_hand")
    public CalculationResponse calculate(@RequestBody CalculationRequest request) {
        CompleteHand completeHand = CompleteHand.of(request.playerWind(), request.vipWind(),
                request.combinationsOrTiles(), request.flags(), request.rules());
        log.debug("Got {}", request);

        CalculationResponse response = new CalculationResponse(
                completeHand.getScore(), completeHand.isMahjong()
        );
        log.debug("Post {}", response);
        return response;
    }
}
