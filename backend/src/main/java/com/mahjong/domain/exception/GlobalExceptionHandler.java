package com.mahjong.domain.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик ошибок
 * */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Ошибки валидации входных данных (неверные плитки, неправильный формат)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        ErrorResponse body = ErrorResponse.create(
                ex,
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorResponse body = ErrorResponse.create(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Что-то пошло не по плану. Повторите попытку позже."
        );
        return ResponseEntity.internalServerError().body(body);
    }
}
