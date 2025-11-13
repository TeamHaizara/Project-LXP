package com.example.projectlxp.exception;


import org.springframework.http.HttpStatus;

public enum ExceptionCode implements ErrorCode {
    // Course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Course not found with id: %d"),
    COURSE_HAS_ENROLLED_STUDENTS(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot delete course with %d enrolled students."),
    COURSE_STATUS_NULL(HttpStatus.BAD_REQUEST, "Course status cannot be null or blank."),
    INVALID_COURSE_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "Invalid status transition: %s -> %s"),
    INVALID_COURSE_STATUS(HttpStatus.BAD_REQUEST, "Invalid course status: %s"),

    // Section
    SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Section not found with id: %d"),
    DUPLICATE_SECTION_ORDER(HttpStatus.CONFLICT, "Duplicate section order: %d"),
    INVALID_SECTION_REORDER_REQUEST(HttpStatus.BAD_REQUEST, "Invalid section reorder request: %s"),
    SECTION_NOT_IN_COURSE(HttpStatus.BAD_REQUEST, "Section %d does not belong to course %d"),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found with id: %d"),
    // TODO:
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "Duplicate category name: %s"),
    CATEGORY_HAS_COURSES(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot delete category with id %d because it has associated courses."),
    CATEGORY_NAME_EQUALS_PRIVIES(HttpStatus.BAD_REQUEST, "Category name is equals privies name."),

    // Lecture
    LECTURE_NOT_IN_SECTION(HttpStatus.BAD_REQUEST, "Lecture %d does not belong to section %d"),

    // Enrolled course
    ALREADY_ENROLLED(HttpStatus.BAD_REQUEST, "Already enrolled"),
    COURSE_BELONGS_TO_USER(HttpStatus.BAD_REQUEST, "Course belongs to user, userId: %d, courseId: %d"),

    // Common
    NOT_NULL_FIELD_IS_NULL(HttpStatus.BAD_REQUEST, "Not null field is null."),

    //Cart
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart not found with id: %d"),
    CART_ALREADY_INCLUDE_COURSE(HttpStatus.BAD_REQUEST, "Course id : %d already include cart"),

    //User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found with id: %d"),

    //Payment
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "Payment failed. try again later"),
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
