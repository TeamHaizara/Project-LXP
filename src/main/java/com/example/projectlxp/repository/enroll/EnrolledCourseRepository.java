package com.example.projectlxp.repository.enroll;

import com.example.projectlxp.model.enroll.EnrolledCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrolledCourseRepository extends JpaRepository<EnrolledCourse, Long> {
}
