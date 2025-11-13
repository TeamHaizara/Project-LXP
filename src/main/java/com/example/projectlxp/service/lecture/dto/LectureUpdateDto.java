package com.example.projectlxp.service.lecture.dto;

import com.example.projectlxp.controller.lecture.request.LectureUpdateRequest;

public record LectureUpdateDto(
        Long courseId,
        Long sectionId,
        Long lectureId,
        LectureUpdateRequest requestDTO,
        Long userId)
{
    public static LectureUpdateDto from(Long courseId,
                                        Long sectionId,
                                        Long lectureId,
                                        LectureUpdateRequest requestDTO,
                                        Long userId) {
        return new LectureUpdateDto(courseId, sectionId, lectureId, requestDTO, userId);
    }
}
