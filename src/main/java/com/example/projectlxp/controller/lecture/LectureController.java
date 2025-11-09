package com.example.projectlxp.controller.lecture;

import com.example.projectlxp.model.lecture.LectureType;
import com.example.projectlxp.service.lecture.LectureServiceImpl;
import com.example.projectlxp.service.lecture.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecture")
public class LectureController {

    private final LectureServiceImpl lectureServiceImpl;

    public LectureController(LectureServiceImpl lectureServiceImpl) {
        this.lectureServiceImpl = lectureServiceImpl;
    }

    // 렉처 생성
    @PostMapping()
    public ResponseEntity<LectureResponseDTO> createLecture(@Valid @RequestBody LectureCreateRequestDTO requestDTO) {
        LectureResponseDTO response = lectureServiceImpl.createLecture(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 렉처 조회 (ID)
    @GetMapping("/{lecture_id}")
    public ResponseEntity<LectureResponseDTO> getLecture(@PathVariable("lecture_id") Long lectureId) {
        LectureResponseDTO response = lectureServiceImpl.getLectureById(lectureId);
        return ResponseEntity.ok(response);
    }

    // 특정 섹션의 렉처 목록 조회
    @GetMapping("/section/{section_id}")
    public ResponseEntity<List<LectureResponseDTO>> getLecturesBySection(@PathVariable("section_id") Long sectionId) {
        List<LectureResponseDTO> response = lectureServiceImpl.getLecturesBySection(sectionId);
        return ResponseEntity.ok(response);
    }

    // 미리보기 가능한 렉처 조회
    @GetMapping("/section/{section_id}/previewable")
    public ResponseEntity<List<LectureResponseDTO>> getPreviewableLectures(@PathVariable("section_id") Long sectionId) {
        List<LectureResponseDTO> response = lectureServiceImpl.getPreviewableLecturesBySection(sectionId);
        return ResponseEntity.ok(response);
    }

    // 타입별 렉처 조회
    @GetMapping("/section/{section_id}/type/{type}")
    public ResponseEntity<List<LectureResponseDTO>> getLecturesByType(
            @PathVariable("section_id") Long sectionId,
            @PathVariable LectureType type) {
        List<LectureResponseDTO> response = lectureServiceImpl.getLecturesBySectionAndType(sectionId, type);
        return ResponseEntity.ok(response);
    }

    // 특정 코스의 모든 렉처 조회
    @GetMapping("/course/{course_id}")
    public ResponseEntity<List<LectureResponseDTO>> getLecturesByCourse(@PathVariable("course_id") Long courseId) {
        List<LectureResponseDTO> response = lectureServiceImpl.getLecturesByCourse(courseId);
        return ResponseEntity.ok(response);
    }

    // 렉처 수정
    @PutMapping("/{lecture_id}")
    public ResponseEntity<LectureResponseDTO> updateLecture(
            @PathVariable("lecture_id") Long lectureId,
            @Valid @RequestBody LectureUpdateRequestDTO requestDTO) {
        LectureResponseDTO response = lectureServiceImpl.updateLecture(lectureId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 렉처 삭제
    @DeleteMapping("/{lecture_id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable("lecture_id") Long lectureId) {
        lectureServiceImpl.deleteLecture(lectureId);
        return ResponseEntity.noContent().build();
    }

    // 렉처 순서 변경
    @PutMapping("/section/{section_id}/reorder")
    public ResponseEntity<Void> reorderLectures(
            @PathVariable("section_id") Long sectionId,
            @RequestBody List<Long> lectureIds) {
        lectureServiceImpl.reorderLectures(sectionId, lectureIds);
        return ResponseEntity.ok().build();
    }
}
