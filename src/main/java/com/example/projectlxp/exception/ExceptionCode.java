package com.example.projectlxp.exception;


import org.springframework.http.HttpStatus;

public enum ExceptionCode implements ErrorCode {
    //course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Course not found with id: "),
    INVALID_COURSE_STATUS(HttpStatus.BAD_REQUEST, "Invalid course status."),

    //section
    SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Section not found with id: "),

    //enrolled course
    ALREADY_ENROLLED(HttpStatus.BAD_REQUEST, "Already enrolled"),

    //common
    NOT_NULL_FIELD_IS_NULL(HttpStatus.BAD_REQUEST, "Not null field is null."),
    ;

    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
