package com.example.projectlxp.repository.course;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;
import com.example.projectlxp.model.section.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {

    // 단일 코스 조회
    @Query("SELECT c FROM Course c WHERE c.id = :id AND c.status != 'DELETED'")
    Optional<Course> findByIdAndNotDeleted(@Param("id") Long id);

    // Course + Section 조회 (fetch join step 1 - 조회용)
    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.sections " +
            "WHERE c.id = :id AND c.status = 'PUBLISHED'")
    Optional<Course> findByIdWithSections(@Param("id") Long id);

    // Course + Section 조회 (관리용 - FETCH JOIN)
    @Query("SELECT DISTINCT c FROM Course c " +
            "JOIN FETCH c.sections s " +
            "WHERE c.id = :id AND c.status != 'DELETED' AND s.deletedAt IS NULL")
    Optional<Course> findByIdWithSectionsForManage(@Param("id") Long id);

    // Section + Lecture 조회 (fetch join step 2)
    @Query("SELECT DISTINCT s FROM Section s " +
            "LEFT JOIN FETCH s.lectures " +
            "WHERE s.course.id = :courseId AND s.deletedAt IS NULL")
    List<Section> findSectionsWithLecturesByCourseId(@Param("courseId") Long courseId);

    // 전체 코스 목록 조회 (발행된 코스만)
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED'")
    List<Course> findAllPublished();

    // 강사별 특정 상태 코스 조회 (조회용: only PUBLISHED)
    @Query("SELECT c FROM Course c WHERE c.instructorId = :instructorId AND c.status = 'PUBLISHED'")
    List<Course> findByInstructorIdAndPublished(@Param("instructorId") Long instructorId);

    // 카테고리별 코스 조회
    @Query("SELECT c FROM Course c WHERE c.categoryId = :categoryId AND c.status = 'PUBLISHED'")
    List<Course> findByCategoryIdAndPublished(@Param("categoryId") Long categoryId);

    // 제목 검색
    @Query("SELECT c FROM Course c WHERE c.title LIKE CONCAT('%', :keyword, '%') AND c.status = 'PUBLISHED'")
    List<Course> searchByTitleAndPublished(@Param("keyword") String keyword);

    // 여러 ID로 코스 조회 (수강 중인 코스 목록용)
    @Query("SELECT c FROM Course c WHERE c.id IN :ids AND c.status = 'PUBLISHED' OR c.status = 'ARCHIVED'")
    List<Course> findByIdsAndPublished(@Param("ids") List<Long> ids);

    // 특정 상태의 코스 조회
    @Query("SELECT c FROM Course c WHERE c.status = :status")
    List<Course> findByStatus(@Param("status") CourseStatus status);

    // 강사별 전체 코스 조회 (관리용: include DRAFT, ARCHIVED)
    @Query("SELECT c FROM Course c WHERE c.instructorId = :instructorId AND c.status != 'DELETED'")
    List<Course> findByInstructorIdAndNotDeleted(@Param("instructorId") Long instructorId);

    // 스케줄러용 물리삭제 대상 조회
    List<Course> findByStatusAndDeletedAtBefore(CourseStatus status, LocalDateTime threshold);
}
