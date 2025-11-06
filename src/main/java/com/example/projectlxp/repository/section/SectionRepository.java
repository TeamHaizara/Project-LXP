package com.example.projectlxp.repository.section;

import com.example.projectlxp.model.section.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    // Soft delete를 고려한 조회
    @Query("SELECT s FROM Section s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Section> findByIdAndNotDeleted(@Param("id") Long id);

    // 삭제되지 않은 모든 섹션 조회
    @Query("SELECT s FROM Section s WHERE s.deletedAt IS NULL")
    List<Section> findAllNotDeleted();

    // 특정 코스의 섹션 조회 (order 정렬, 삭제되지 않은 것만)
    @Query("SELECT s FROM Section s WHERE s.course.id = :courseId AND s.deletedAt IS NULL ORDER BY s.order ASC")
    List<Section> findByCourseIdAndNotDeletedOrderByOrder(@Param("courseId") Long courseId);

    // 특정 코스의 섹션 개수 조회 (삭제되지 않은 것만)
    @Query("SELECT COUNT(s) FROM Section s WHERE s.course.id = :courseId AND s.deletedAt IS NULL")
    Long countByCourseIdAndNotDeleted(@Param("courseId") Long courseId);

    // 특정 코스의 최대 order 값 조회
    @Query("SELECT MAX(s.order) FROM Section s WHERE s.course.id = :courseId AND s.deletedAt IS NULL")
    Optional<Integer> findMaxOrderByCourseId(@Param("courseId") Long courseId);
}
