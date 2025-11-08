package com.example.projectlxp.config.web;

import com.example.projectlxp.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleException(BusinessException e) {
        HttpStatus status = e.getHttpStatus();
        Map<String, Object> body = Map.of("errorMessage", e.getMessage());

        return ResponseEntity.status(status).body(body);
    }
}
