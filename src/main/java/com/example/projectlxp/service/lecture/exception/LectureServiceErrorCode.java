package com.example.projectlxp.service.lecture.exception;

import com.example.projectlxp.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum LectureServiceErrorCode implements ErrorCode {
    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "Lecture not found with id: "),
    ;

    private HttpStatus httpStatus;
    private String message;

    LectureServiceErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
