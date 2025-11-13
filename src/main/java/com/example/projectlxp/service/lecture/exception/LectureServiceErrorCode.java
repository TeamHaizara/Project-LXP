package com.example.projectlxp.service.lecture.exception;

import com.example.projectlxp.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum LectureServiceErrorCode implements ErrorCode {
    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "Lecture not found with id: %d"),
    LECTURE_NOT_INCLUDED_SECTION(HttpStatus.BAD_REQUEST, "Lecture id : %d not included section id : %d\n"),
    LECTURE_NOT_INCLUDED_COURSE(HttpStatus.BAD_REQUEST, "Lecture id : %d not included course"),
    SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Section not found with id: %d"),
    NOT_ENROLLED_COURSE(HttpStatus.BAD_REQUEST, "User id : %d is not enrolled in course id : %d"),
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
