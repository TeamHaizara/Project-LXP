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
        this.message = String.format(exceptionCode.getMessage(), field);
        this.httpStatus = exceptionCode.getStatus();
    }

    private BusinessException(HttpStatus httpStatus, String message, Throwable cause){
        super(message, cause);
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public BusinessException(ErrorCode exceptionCode, Long id1, Long id2){
        this.message = String.format(exceptionCode.getMessage(), id1.toString(), id2.toString());
        this.httpStatus = exceptionCode.getStatus();
    }

    public BusinessException(ErrorCode exceptionCode, String param1, String param2){
        this.message = String.format(exceptionCode.getMessage(), param1, param2);
        this.httpStatus = exceptionCode.getStatus();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
