package com.example.projectlxp.repository.lecture;

import com.example.projectlxp.model.lecture.Lecture;
import com.example.projectlxp.model.lecture.LectureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    // Soft delete를 고려한 조회
    @Query("SELECT l FROM Lecture l WHERE l.id = :id AND l.status = false")
    Optional<Lecture> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    // 삭제되지 않은 모든 렉처 조회
    @Query("SELECT l FROM Lecture l WHERE l.status = false")
    List<Lecture> findAllNotDeleted();

    // 특정 섹션의 렉처 조회 (order 정렬, 삭제되지 않은 것만)
    @Query("SELECT l FROM Lecture l WHERE l.section.id = :sectionId AND l.status = false ORDER BY l.sortOrder ASC")
    List<Lecture> findBySectionIdAsc(Long sectionId);

    // 특정 섹션의 렉처 개수 조회 (삭제되지 않은 것만)
    @Query("SELECT COUNT(l) FROM Lecture l WHERE l.section.id = :sectionId AND l.status = false")
    Long countBySectionId(Long sectionId);

    // 특정 섹션의 최대 order 값 조회
    @Query("SELECT MAX(l.sortOrder) FROM Lecture l WHERE l.section.id = :sectionId AND l.status = false")
    Optional<Integer> findMaxOrderBySectionId(@Param("sectionId") Long sectionId);

    // 미리보기 가능한 렉처 조회 (특정 섹션)
    @Query("SELECT l FROM Lecture l WHERE l.section.id = :sectionId AND l.previewable = true AND l.status = false ORDER BY l.sortOrder ASC")
    List<Lecture> findBySectionIdPreviewableTrueAsc(Long sectionId);

    // 타입별 렉처 조회 (특정 섹션)
    @Query("SELECT l FROM Lecture l WHERE l.section.id = :sectionId AND l.type = :type AND l.status = false ORDER BY l.sortOrder ASC")
    List<Lecture> findBySectionIdAndTypeAsc(Long sectionId, LectureType type);

    // 특정 코스의 모든 렉처 조회 (섹션을 통해 조인)
    @Query("SELECT l FROM Lecture l JOIN l.section s WHERE s.course.id = :courseId AND l.status = false AND s.deletedAt IS NULL ORDER BY s.order ASC, l.sortOrder ASC")
    List<Lecture> findBySectionCourseIdAsc(Long courseId);

    // id 리스트를 통해 삭제 되지 않은 렉처 조회
    List<Lecture> findAllByIdInAndStatusIsFalse(List<Long> ids);

}
