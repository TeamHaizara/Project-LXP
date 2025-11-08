package com.example.projectlxp.model.lecture.exception;

import com.example.projectlxp.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum LectureExceptionCode implements ErrorCode {
    ORDER_NUMBER_UNDER_ZERO(HttpStatus.BAD_REQUEST,"Order number cannot be less than 0."),
    LECTURE_ALREADY_DELETED(HttpStatus.BAD_REQUEST,"Deleted lectures cannot be changed.");


    private final HttpStatus status;
    private final String message;

    LectureExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
    @Override
    public HttpStatus getStatus() {
        return null;
    }

    @Override
    public String getMessage() {
        return "";
    }
}
