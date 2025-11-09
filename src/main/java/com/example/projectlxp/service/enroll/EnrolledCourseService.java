package com.example.projectlxp.service.enroll;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.enroll.EnrolledCourse;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.enroll.EnrolledCourseRepository;
import com.example.projectlxp.service.enroll.dto.EnrollCourseServiceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EnrolledCourseService {
    private final EnrolledCourseRepository enrolledCourseRepository;
    private final CourseRepository courseRepository;

    public EnrolledCourseService(
        EnrolledCourseRepository enrolledCourseRepository,
        CourseRepository courseRepository
    ) {
        this.enrolledCourseRepository = enrolledCourseRepository;
        this.courseRepository = courseRepository;
    }

    public void enroll(EnrollCourseServiceDto dto) {
        validateExistUser(dto.userId());
        validateExistCourse(dto.courseId());

        enrolledCourseRepository.save(dto.toEntity());
    }

    public List<Course> getEnrolledCourses(Long userId) {
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findEnrolledCoursesByUserId(userId);

        return courseRepository.findByIdsAndNotDeleted(
            enrolledCourses.stream().map(EnrolledCourse::getCourseId).toList()
        );
    }

    private void validateExistUser(Long userId) {
        //TODO()
    }

    private void validateExistCourse(Long courseId) {
        courseRepository.findById(courseId)
            .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND));
    }
}
