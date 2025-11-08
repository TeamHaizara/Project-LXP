package com.example.projectlxp.service.lecture;

import com.example.projectlxp.model.lecture.LectureType;
import com.example.projectlxp.service.lecture.dto.LectureCreateRequestDTO;
import com.example.projectlxp.service.lecture.dto.LectureResponseDTO;
import com.example.projectlxp.service.lecture.dto.LectureUpdateRequestDTO;

import java.util.List;

public interface LectureService {
    // 렉처 생성
    LectureResponseDTO createLecture(LectureCreateRequestDTO requestDTO);
    // 렉처 조회 (ID)
    LectureResponseDTO getLectureById(Long id);
    // 특정 섹션의 모든 렉처 조회
    List<LectureResponseDTO> getLecturesBySection(Long sectionId);
    // 미리보기 가능한 렉처 조회
    List<LectureResponseDTO> getPreviewableLecturesBySection(Long sectionId);
    // 특정 타입의 렉처 조회
    List<LectureResponseDTO> getLecturesBySectionAndType(Long sectionId, LectureType type);
    // 특정 코스의 모든 렉처 조회
    List<LectureResponseDTO> getLecturesByCourse(Long courseId);
    // 렉처 수정
    LectureResponseDTO updateLecture(Long id, LectureUpdateRequestDTO requestDTO);
    // 렉처 삭제 (Soft Delete)
    void deleteLecture(Long id);
    // 렉처 순서 변경
    void reorderLectures(Long sectionId, List<Long> lectureIds);
}
