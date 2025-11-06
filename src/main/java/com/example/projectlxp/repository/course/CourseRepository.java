package com.example.projectlxp.repository.course;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Soft delete를 고려한 조회
    @Query("SELECT c FROM Course c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Course> findByIdAndNotDeleted(@Param("id") Long id);

    // 삭제되지 않은 모든 코스 조회
    @Query("SELECT c FROM Course c WHERE c.deletedAt IS NULL")
    List<Course> findAllNotDeleted();

    // 강사별 코스 조회 (삭제되지 않은 것만)
    @Query("SELECT c FROM Course c WHERE c.instructorId = :instructorId AND c.deletedAt IS NULL")
    List<Course> findByInstructorIdAndNotDeleted(@Param("instructorId") Long instructorId);

    // 카테고리별 코스 조회 (삭제되지 않은 것만)
    @Query("SELECT c FROM Course c WHERE c.categoryId = :categoryId AND c.deletedAt IS NULL")
    List<Course> findByCategoryIdAndNotDeleted(@Param("categoryId") Long categoryId);

    // 상태별 코스 조회 (삭제되지 않은 것만)
    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.deletedAt IS NULL")
    List<Course> findByStatusAndNotDeleted(@Param("status") CourseStatus status);

    // 강사 ID와 상태로 코스 조회
    @Query("SELECT c FROM Course c WHERE c.instructorId = :instructorId AND c.status = :status AND c.deletedAt IS NULL")
    List<Course> findByInstructorIdAndStatusAndNotDeleted(@Param("instructorId") Long instructorId, @Param("status") CourseStatus status);

    // 제목으로 검색 (LIKE, 삭제되지 않은 것만)
    @Query("SELECT c FROM Course c WHERE c.title LIKE %:keyword% AND c.deletedAt IS NULL")
    List<Course> searchByTitleAndNotDeleted(@Param("keyword") String keyword);
}
