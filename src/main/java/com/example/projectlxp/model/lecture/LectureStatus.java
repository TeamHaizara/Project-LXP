package com.example.projectlxp.model.lecture;

import static com.example.projectlxp.model.lecture.exception.LectureExceptionCode.INVALID_LECTURE_STATE;
import static com.example.projectlxp.model.lecture.exception.LectureExceptionCode.LECTURE_STATUS_NOT_NULL;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.Status;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum LectureStatus implements Status {

        DELETED("조회 불가한 상태"),
        DRAFT("발행되지 않은 임시 작성 상태"),
        PUBLISHED("발행되어 강의 시청이 가능한 상태");

        private final String description;

        LectureStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return this.name();
        }

        // 문자열에서 CourseStatus로 변환
        public static LectureStatus from(String status) {
            if (status == null || status.isBlank()) {
                // TODO - use custom exception
                throw BusinessException.builder(LECTURE_STATUS_NOT_NULL).build();
            }
            try {
                return LectureStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // TODO - use custom exception
                throw BusinessException.builder(INVALID_LECTURE_STATE).withField(status).build();
            }
        }

        /**
         * 상태 전이 가능 여부 검증
         * 1. DRAFT → PUBLISHED or DELETED
         * 2. PUBLISHED ↔ ARCHIVED (양방향)
         * 3. ARCHIVED → DELETED
         * 4. DELETED: 최종 상태, 다른 상태로 전이 불가
         */
        public boolean canTransitionTo(LectureStatus newStatus) {
            if ((newStatus == null) || (this == DELETED)) {
                return false;
            }
            return switch (this) {
                case DRAFT -> newStatus == PUBLISHED || newStatus == DELETED;
                case PUBLISHED -> newStatus == DELETED;
                case DELETED -> false;
            };
        }

        /**
         * 현재 상태에서 전이 가능한 상태들 반환
         */
        public Set<LectureStatus> getAvailableTransitions() {
            return switch (this) {
                case DRAFT -> new HashSet<>(Arrays.asList(PUBLISHED, DELETED));
                case PUBLISHED -> new HashSet<>(List.of(DELETED));
                case DELETED -> new HashSet<>();
            };

    }

}
