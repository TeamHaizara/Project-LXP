package com.example.projectlxp.controller.lecture;

import com.example.projectlxp.controller.lecture.response.LectureResponse;
import com.example.projectlxp.service.lecture.LectureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }
    
    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureResponse> getLecture(@PathVariable Long lectureId) {
        LectureResponse response = lectureService.getLectureById(lectureId);
        return ResponseEntity.ok(response);
    }
}
