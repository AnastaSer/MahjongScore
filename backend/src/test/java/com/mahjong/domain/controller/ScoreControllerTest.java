package com.mahjong.domain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahjong.domain.dto.CalculationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void calculate_ValidRequest_ReturnsScore() throws Exception {
        // Подготовка запроса
        var request = new CalculationRequest(
                "west",
                "east",
                List.of("открытая пара дракон зеленый",  "закрытая тройка дракон красный",
                        "закрытая четверка дракон белый", "закрытая тройка ветер восточный", "закрытая четвёрка ветер северный"),
                List.of("завершение свободной костью", "маджонг"),
                "классика"
        );

        // Выполнение и проверка
        mockMvc.perform(post("/api/mahjong_scores/calculate_one_hand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").isNumber())
                .andExpect(jsonPath("$.score").value(2000))
                .andExpect(jsonPath("$.isMahjong").isBoolean())
                .andExpect(jsonPath("$.isMahjong").value(true));

        // Подготовка запроса
        request = new CalculationRequest(
                "west",
                "east",
                List.of("открытая пара дракон зеленый",  "закрытая тройка дракон красный",
                        "закрытая четверка дракон белый", "закрытая тройка ветер восточный", "закрытая четвёрка ветер северный"),
                List.of("завершение свободной костью", "маджонг"),
                "друзья"
        );

        // Выполнение и проверка
        mockMvc.perform(post("/api/mahjong_scores/calculate_one_hand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").isNumber())
                .andExpect(jsonPath("$.score").value(81920))
                .andExpect(jsonPath("$.isMahjong").isBoolean())
                .andExpect(jsonPath("$.isMahjong").value(true));
    }

    @Test
    void calculate_InvalidTile_ReturnsBadRequest() throws Exception {
        var request = new CalculationRequest(
                "west", "east",
                List.of("открытая пара дракон"),
                List.of(),
                "классика"
        );

        mockMvc.perform(post("/api/mahjong_scores/calculate_one_hand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.body.detail").value(
                        "Ошибка в параметрах: 'открытая пара дракон'. А именно: Введите точное описание случая: 'открытая пара дракон'"
                ));
    }

    @Test
    void calculate_EmptyTiles_Returns0() throws Exception {
        var request = new CalculationRequest(
                "west", "east",
                List.of(),
                List.of(),
                ""
        );

        mockMvc.perform(post("/api/mahjong_scores/calculate_one_hand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").isNumber())
                .andExpect(jsonPath("$.score").value(0))
                .andExpect(jsonPath("$.isMahjong").isBoolean())
                .andExpect(jsonPath("$.isMahjong").value(false));
    }
}