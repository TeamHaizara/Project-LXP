package com.example.projectlxp.model.course;

import com.example.projectlxp.exception.BusinessException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.example.projectlxp.exception.ExceptionCode.*;

public enum CourseStatus {

    DELETED("완전히 삭제되어 조회 불가한 상태"),
    DRAFT("발행되지 않은 임시 작성 상태"),
    PUBLISHED("발행되어 수강 신청이 가능한 상태"),
    ARCHIVED("신규 수강이 불가하며 기존 수강자만 조회 가능한 상태");

    private final String description;

    CourseStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // 문자열에서 CourseStatus로 변환
    public static CourseStatus from(String status) {
        if (status == null || status.isBlank()) {
            throw new BusinessException(COURSE_STATUS_NULL);
        }
        try {
            return CourseStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(INVALID_COURSE_STATUS, status);
        }
    }

    /**
     * 상태 전이 가능 여부 검증
     * 1. DRAFT → PUBLISHED or DELETED
     * 2. PUBLISHED ↔ ARCHIVED (양방향)
     * 3. ARCHIVED → DELETED
     * 4. DELETED: 최종 상태, 다른 상태로 전이 불가
     */
    public boolean canTransitionTo(CourseStatus newStatus) {
        if ((newStatus == null) || (this == DELETED)) {
            return false;
        }
        return switch (this) {
            case DRAFT -> newStatus == PUBLISHED || newStatus == DELETED;
            case PUBLISHED -> newStatus == ARCHIVED || newStatus == DELETED;
            case ARCHIVED -> newStatus == PUBLISHED || newStatus == DELETED;
            case DELETED -> false;
        };
    }

    /**
     * 현재 상태에서 전이 가능한 상태들 반환
     */
    public Set<CourseStatus> getAvailableTransitions() {
        return switch (this) {
            case DRAFT -> new HashSet<>(Arrays.asList(PUBLISHED, DELETED));
            case PUBLISHED -> new HashSet<>(Arrays.asList(ARCHIVED, DELETED));
            case ARCHIVED -> new HashSet<>(Arrays.asList(PUBLISHED, DELETED));
            case DELETED -> new HashSet<>();
        };
    }
}
