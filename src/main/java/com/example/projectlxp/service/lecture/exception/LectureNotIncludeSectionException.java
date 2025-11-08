package com.example.projectlxp.service.lecture.exception;

import com.example.projectlxp.exception.BusinessException;

import static com.example.projectlxp.service.lecture.exception.LectureServiceErrorCode.LECTURE_NOT_INCLUDED_SECTION;

public class LectureNotIncludeSectionException extends BusinessException {
    public LectureNotIncludeSectionException(Long lectureId, Long sectionId) {
        super(LECTURE_NOT_INCLUDED_SECTION, lectureId, sectionId);
    }
}
