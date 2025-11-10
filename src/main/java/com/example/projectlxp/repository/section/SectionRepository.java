package com.example.projectlxp.repository.section;

import com.example.projectlxp.model.section.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findByIdAndDeletedAtIsNull(Long sectionId);

    @Query("SELECT s FROM Section s WHERE s.course.id = :courseId AND s.deletedAt IS NULL ORDER BY s.order")
    List<Section> findByCourseIdAndNotDeleted(@Param("courseId") Long courseId);

    // 특정 강좌 내에서 order 값이 중복되는지 확인하기 위한 쿼리
    boolean existsByCourseIdAndOrderAndDeletedAtIsNull(Long courseId, Integer order);
}
