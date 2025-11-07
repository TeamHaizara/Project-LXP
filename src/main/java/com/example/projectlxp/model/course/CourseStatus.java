package com.example.projectlxp.model.course;

public enum CourseStatus {

    DELETED("완전히 삭제되어 조회 불가한 상태"),
    DRAFT("발행되지 않은 임시 작성 상태"),
    PUBLISHED("발행되어 수강 신청이 가능한 상태"),
    ARCHIVED("신규 수강이 불가하며 기존 수강자만 조회 가능한 상태");

    private final String description;

    CourseStatus(String description) {
        this.description = description;
    }

}
