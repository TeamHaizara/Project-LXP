package com.example.projectlxp.model.lecture.exception;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ErrorCode;

import static com.example.projectlxp.model.lecture.exception.LectureExceptionCode.ORDER_NUMBER_UNDER_ZERO;

public class LectureOrderBoundException extends BusinessException {
    public LectureOrderBoundException() {
        super(ORDER_NUMBER_UNDER_ZERO);
    }
}
