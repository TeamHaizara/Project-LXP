package com.example.projectlxp.Exception;

import org.springframework.http.HttpStatus;

public enum ExceptionCode {

    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Course not found with id: "),
    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "Lecture not found with id: "),
    SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Section not found with id: "),
    INVALID_COURSE_STATUS(HttpStatus.BAD_REQUEST, "Invalid course status.");

    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
