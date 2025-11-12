package com.example.projectlxp.repository.lecture;

import com.example.projectlxp.model.lecture.Lecture;
import com.example.projectlxp.model.lecture.LectureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    // Soft delete를 고려한 조회
    @Query("SELECT l FROM Lecture l WHERE l.id = :id AND l.status IS FALSE")
    Optional<Lecture> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    // Section과 Course를 함께 조회
    @Query("SELECT l FROM Lecture l " +
            "JOIN FETCH l.section s " +
            "JOIN FETCH s.course c " +
            "WHERE l.id = :id AND l.status IS FALSE")
    Optional<Lecture> findByIdWithSectionAndCourse(@Param("id") Long id);

    // 삭제되지 않은 모든 렉처 조회
    @Query("SELECT l FROM Lecture l WHERE l.status IS FALSE")
    List<Lecture> findAllNotDeleted();

    // 특정 섹션의 렉처 조회 (order 정렬, 삭제되지 않은 것만)
    @Query("SELECT l FROM Lecture l WHERE l.section.id = :sectionId AND l.status IS FALSE ORDER BY l.sortOrder ASC")
    List<Lecture> findBySectionIdAsc(Long sectionId);

    // 특정 섹션의 렉처 개수 조회 (삭제되지 않은 것만)
    @Query("SELECT COUNT(l) FROM Lecture l WHERE l.section.id = :sectionId AND l.status IS FALSE")
    Long countBySectionId(Long sectionId);

    // 특정 섹션의 최대 order 값 조회
    @Query("SELECT MAX(l.sortOrder) FROM Lecture l WHERE l.section.id = :sectionId AND l.status IS FALSE")
    Optional<Integer> findMaxOrderBySectionId(@Param("sectionId") Long sectionId);

    // 미리보기 가능한 렉처 조회 (특정 섹션)
    @Query("SELECT l FROM Lecture l WHERE l.section.id = :sectionId AND l.previewable IS TRUE AND l.status IS FALSE ORDER BY l.sortOrder ASC")
    List<Lecture> findBySectionIdPreviewableTrueAsc(Long sectionId);

    // 타입별 렉처 조회 (특정 섹션)
    @Query("SELECT l FROM Lecture l WHERE l.section.id = :sectionId AND l.type = :type AND l.status IS FALSE ORDER BY l.sortOrder ASC")
    List<Lecture> findBySectionIdAndTypeAsc(Long sectionId, LectureType type);

    // 특정 코스의 모든 렉처 조회 (섹션을 통해 조인)
    @Query("SELECT l FROM Lecture l JOIN l.section s WHERE s.course.id = :courseId AND l.status IS FALSE AND s.deletedAt IS NULL ORDER BY s.order ASC, l.sortOrder ASC")
    List<Lecture> findBySectionCourseIdAsc(Long courseId);

    // id 리스트를 통해 삭제 되지 않은 렉처 조회
    List<Lecture> findAllByIdInAndStatusIsFalse(List<Long> ids);

    //스케줄러용 물리삭제 메서드
    List<Lecture> findByStatusIsTrueAndDeletedAtBefore(LocalDateTime threshold);
}
