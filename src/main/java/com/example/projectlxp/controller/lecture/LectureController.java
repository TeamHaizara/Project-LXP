package com.example.projectlxp.controller.lecture;

import com.example.projectlxp.controller.lecture.response.LectureResponse;
import com.example.projectlxp.service.lecture.LectureService;
import com.example.projectlxp.service.user.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping("/learner/lectures/{lectureId}")
    public ResponseEntity<LectureResponse> getLecture(@PathVariable Long lectureId,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        LectureResponse response = lectureService.getLectureById(lectureId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }
}
