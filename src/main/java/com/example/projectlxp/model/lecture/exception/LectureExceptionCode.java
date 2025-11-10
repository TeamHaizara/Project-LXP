package com.example.projectlxp.model.lecture.exception;

import com.example.projectlxp.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum LectureExceptionCode implements ErrorCode {
    ORDER_NUMBER_UNDER_ZERO(HttpStatus.BAD_REQUEST,"Order number cannot be less than 0."),
    LECTURE_ALREADY_DELETED(HttpStatus.BAD_REQUEST,"Deleted lectures cannot be changed."),
    LECTURE_STATUS_NOT_NULL(HttpStatus.BAD_REQUEST,"상태 값은 null이거나 공백일 수 없습니다."),
    INVALID_LECTURE_STATE(HttpStatus.BAD_REQUEST,"유효하지 않은 렉처 상태입니다 요청 렉처 상태 : %s"),
    ;


    private final HttpStatus status;
    private final String message;

    LectureExceptionCode(HttpStatus status, String message) {
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
