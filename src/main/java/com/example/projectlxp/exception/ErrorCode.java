package com.example.projectlxp.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getStatus();
    String getMessage();
}