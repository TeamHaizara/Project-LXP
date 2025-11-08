package com.example.projectlxp.exception;


import org.springframework.http.HttpStatus;

public enum ExceptionCode implements ErrorCode {
    // Course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Course not found with id: %d"),
    COURSE_HAS_ENROLLED_STUDENTS(HttpStatus.CONFLICT, "Cannot delete course with %d enrolled students."),
    COURSE_STATUS_NULL(HttpStatus.BAD_REQUEST, "Course status cannot be null or blank."),
    INVALID_COURSE_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "Invalid status transition: %s -> %s"),
    INVALID_COURSE_STATUS(HttpStatus.BAD_REQUEST, "Invalid course status: %s"),

    // Section
    SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Section not found with id: "),

    // Lecture

    // Enrolled course

    // Common
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
