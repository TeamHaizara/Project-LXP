package com.example.projectlxp.controller.lecture.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LectureListResponse {
    private final List<LectureResponse> lectureList;

    public LectureListResponse(List<LectureResponse> values) {
        this.lectureList = new ArrayList<>(values);
    }

    public List<LectureResponse> getValues() {
        return Collections.unmodifiableList(lectureList);
    }

    // --- 여기에 컬렉션 관련 비즈니스 로직 추가 ---

    public LectureListResponse getPreviewableLectures() {
        List<LectureResponse> filtered = this.lectureList.stream()
                .filter(LectureResponse::getPreviewable)
                .toList();
        return new LectureListResponse(filtered);
    }


    public Integer getTotalDuration() {
        return lectureList.stream().mapToInt(LectureResponse::getDuration).sum();
    }


    public boolean isEmpty() {
        return this.lectureList.isEmpty();
    }
}

