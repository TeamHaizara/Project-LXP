package com.example.projectlxp.service.lecture.exception;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ErrorCode;

import static com.example.projectlxp.service.lecture.exception.LectureServiceErrorCode.LECTURE_NOT_FOUND;

public class LectureNotFoundException extends BusinessException {
    public LectureNotFoundException() {
        super(LECTURE_NOT_FOUND);
    }

    public LectureNotFoundException(long id) {
        super(LECTURE_NOT_FOUND,id);
    }
}
