package com.example.projectlxp.service.lecture;

import com.example.projectlxp.controller.lecture.response.LectureListResponse;
import com.example.projectlxp.model.lecture.LectureType;
import com.example.projectlxp.controller.lecture.request.LectureCreateRequest;
import com.example.projectlxp.controller.lecture.response.LectureResponse;
import com.example.projectlxp.controller.lecture.request.LectureUpdateRequest;

import java.util.List;

public interface LectureService {
    // 렉처 생성
    LectureResponse createLecture(LectureCreateRequest requestDTO);

    // 렉처 조회 (ID)
    LectureResponse getLectureById(Long id);

    // 특정 섹션의 모든 렉처 조회
    LectureListResponse getLecturesBySection(Long sectionId);

    // 미리보기 가능한 렉처 조회
    LectureListResponse getPreviewableLecturesBySection(Long sectionId);

    // 특정 타입의 렉처 조회
    LectureListResponse getLecturesBySectionAndType(Long sectionId, LectureType type);

    // 특정 코스의 모든 렉처 조회
    LectureListResponse getLecturesByCourse(Long courseId);

    // 렉처 수정
    LectureResponse updateLecture(Long id, LectureUpdateRequest requestDTO);

    // 렉처 삭제 (Soft Delete)
    void deleteLecture(Long id);

    // 렉처 순서 변경
    void reorderLectures(Long sectionId, List<Long> lectureIds);
}
