package com.example.projectlxp.exception;


import org.springframework.http.HttpStatus;

public enum ExceptionCode implements ErrorCode {
    // Course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Course not found with id: %d"),
    COURSE_HAS_ENROLLED_STUDENTS(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot delete course with %d enrolled students."),
    COURSE_STATUS_NULL(HttpStatus.BAD_REQUEST, "Course status cannot be null or blank."),
    INVALID_COURSE_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "Invalid status transition: %s -> %s"),
    INVALID_COURSE_STATUS(HttpStatus.BAD_REQUEST, "Invalid course status: %s"),
    NOT_COURSE_INSTRUCTOR(HttpStatus.FORBIDDEN, "User %d is not the instructor of course %d"),

    // Section
    SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Section not found with id: %d"),
    DUPLICATE_SECTION_ORDER(HttpStatus.CONFLICT, "Duplicate section order: %d"),
    INVALID_SECTION_REORDER_REQUEST(HttpStatus.BAD_REQUEST, "The provided section IDs do not match the sections in the course"),
    SECTION_NOT_IN_COURSE(HttpStatus.BAD_REQUEST, "Section %d does not belong to course %d"),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found with id: %d"),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "Duplicate category name: %s"),
    CATEGORY_HAS_COURSES(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot delete category with id %d because it has associated courses."),
    CATEGORY_NAME_UNCHANGED(HttpStatus.BAD_REQUEST, "Category name is unchanged."),

    // Lecture
    LECTURE_NOT_IN_SECTION(HttpStatus.BAD_REQUEST, "Lecture %d does not belong to section %d"),
    INVALID_LECTURE_REORDER_REQUEST(HttpStatus.BAD_REQUEST, "The provided lecture IDs do not match the lectures in the section"),

    // Enrolled course
    ALREADY_ENROLLED(HttpStatus.BAD_REQUEST, "User is already enrolled in course %d"),

    // Common
    NOT_NULL_FIELD_IS_NULL(HttpStatus.BAD_REQUEST, "Not null field is null."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "Payment failed. Please try again later."),

    //User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found with id: %d");

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
