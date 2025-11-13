package com.example.projectlxp.service.lecture;

import com.example.projectlxp.controller.lecture.request.LectureCreateRequest;
import com.example.projectlxp.controller.lecture.request.LectureUpdateRequest;
import com.example.projectlxp.controller.lecture.response.LectureListResponse;
import com.example.projectlxp.controller.lecture.response.LectureResponse;
import com.example.projectlxp.model.lecture.LectureType;
import java.util.List;

public interface LectureService {
    // 렉처 생성
    LectureResponse createLecture(Long courseId, Long sectionId, LectureCreateRequest requestDTO, Long userId);

    // 렉처 조회 (ID)
    LectureResponse getLectureById(Long lectureId,Long userId);

    // 특정 섹션의 모든 렉처 조회
    LectureListResponse getLecturesBySection(Long sectionId);

    // 미리보기 가능한 렉처 조회
    LectureListResponse getPreviewableLecturesBySection(Long sectionId);

    // 특정 타입의 렉처 조회
    LectureListResponse getLecturesBySectionAndType(Long sectionId, LectureType type);

    // 렉처 수정
    LectureResponse updateLecture(Long courseId, Long sectionId, Long lectureId, LectureUpdateRequest requestDTO, Long userId);

    // 렉처 삭제 (Soft Delete)
    void deleteLecture(Long courseId, Long sectionId, Long lectureId, Long userId);

    // 렉처 순서 변경
    void reorderLectures(Long courseId, Long sectionId, List<Long> lectureIds, Long userId);
}
