package com.example.projectlxp.controller.lecture;

import com.example.projectlxp.controller.lecture.request.LectureCreateRequest;
import com.example.projectlxp.controller.lecture.response.LectureListResponse;
import com.example.projectlxp.controller.lecture.response.LectureResponse;
import com.example.projectlxp.controller.lecture.request.LectureUpdateRequest;
import com.example.projectlxp.service.lecture.LectureServiceImpl;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class LectureController {

    private final LectureServiceImpl lectureServiceImpl;

    public LectureController(LectureServiceImpl lectureServiceImpl) {
        this.lectureServiceImpl = lectureServiceImpl;
    }

    // 렉처 생성 -> 색션
    @PostMapping("/sections/{section_id}/lectures")
    public ResponseEntity<LectureResponse> createLecture(
            @PathVariable("section_id") Long sectionId,
            @Valid @RequestBody LectureCreateRequest requestDTO) {
        LectureResponse response = lectureServiceImpl.createLecture(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 렉처 조회 (ID)
    @GetMapping("/lectures")
    public ResponseEntity<LectureResponse> getLecture(@RequestParam("lecture_id") Long lectureId) {
        LectureResponse response = lectureServiceImpl.getLectureById(lectureId);
        return ResponseEntity.ok(response);
    }

    // 특정 섹션의 렉처 목록 조회 -> 색션
    @GetMapping("/sections/{section_id}/lectures")
    public ResponseEntity<LectureListResponse> getLecturesBySection(
            @PathVariable("section_id") Long sectionId) {
        LectureListResponse response = lectureServiceImpl.getLecturesBySection(sectionId);
        return ResponseEntity.ok(response);
    }

    // 미리보기 가능한 렉처 조회 -> 코스
    @GetMapping("/sections/{section_id}/lectures/previewable")
    public ResponseEntity<LectureListResponse> getPreviewableLectures(
            @PathVariable("section_id") Long sectionId) {
        LectureListResponse response = lectureServiceImpl.getPreviewableLecturesBySection(sectionId);
        return ResponseEntity.ok(response);
    }

    // 렉처 수정
    @PutMapping("/lectures/{lecture_id}")
    public ResponseEntity<LectureResponse> updateLecture(
            @PathVariable("lecture_id") Long lectureId,
            @Valid @RequestBody LectureUpdateRequest requestDTO) {
        LectureResponse response = lectureServiceImpl.updateLecture(lectureId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 렉처 삭제
    @DeleteMapping("/lectures/{lecture_id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable("lecture_id") Long lectureId) {
        lectureServiceImpl.deleteLecture(lectureId);
        return ResponseEntity.noContent().build();
    }

    // 렉처 순서 변경 -> course
    @PutMapping("/course/{course_id}/lectures/reorder")
    public ResponseEntity<Void> reorderLectures(
            @PathVariable("course_id") Long sectionId,
            @RequestBody List<Long> lectureIds) {
        lectureServiceImpl.reorderLectures(sectionId, lectureIds);
        return ResponseEntity.ok().build();
    }
}