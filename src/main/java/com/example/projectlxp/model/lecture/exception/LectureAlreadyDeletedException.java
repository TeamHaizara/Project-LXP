package com.example.projectlxp.model.lecture.exception;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ErrorCode;

import static com.example.projectlxp.model.lecture.exception.LectureExceptionCode.LECTURE_ALREADY_DELETED;

public class LectureAlreadyDeletedException extends BusinessException {
    public LectureAlreadyDeletedException() {
        super(LECTURE_ALREADY_DELETED);
    }
}
