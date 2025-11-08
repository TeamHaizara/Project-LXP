package com.example.projectlxp.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;
    private Throwable cause = null;

    public BusinessException(ErrorCode exceptionCode){
        this.message = exceptionCode.getMessage();
        this.httpStatus = exceptionCode.getStatus();
    }

    public BusinessException(ErrorCode exceptionCode, Long id){
        this.message = exceptionCode.getMessage() + id.toString();
        this.httpStatus = exceptionCode.getStatus();
    }

    public BusinessException(ErrorCode exceptionCode, String field){
        this.message = exceptionCode.getMessage() + field;
        this.httpStatus = exceptionCode.getStatus();
    }

    private BusinessException(HttpStatus httpStatus, String message, Throwable cause){
        super(message, cause);
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
