package com.example.projectlxp.Exception;

public class BusinessException extends RuntimeException {

    private final ExceptionCode exceptionCode;
    private final Long id;

    public BusinessException(ExceptionCode exceptionCode){
        this.exceptionCode = exceptionCode;
        this.id = null;
    }

    public BusinessException(ExceptionCode exceptionCode, Long id){
        this.exceptionCode = exceptionCode;
        this.id = id;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return exceptionCode.toString() + (hasId() ? id : "");
    }

    private boolean hasId() {
        return id != null;
    }

}
