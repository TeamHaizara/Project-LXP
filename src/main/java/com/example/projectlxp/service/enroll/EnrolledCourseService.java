package com.example.projectlxp.service.enroll;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.enroll.EnrolledCourse;
import com.example.projectlxp.model.user.User;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.enroll.EnrolledCourseRepository;
import com.example.projectlxp.repository.user.UserRepository;
import com.example.projectlxp.service.enroll.dto.EnrollCourseServiceDto;
import com.example.projectlxp.service.payment.PaymentService;
import com.example.projectlxp.service.payment.dto.PaymentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class EnrolledCourseService {
    private final EnrolledCourseRepository enrolledCourseRepository;
    private final CourseRepository courseRepository;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public EnrolledCourseService(
            EnrolledCourseRepository enrolledCourseRepository,
            CourseRepository courseRepository,
            PaymentService paymentService, UserRepository userRepository
    ) {
        this.enrolledCourseRepository = enrolledCourseRepository;
        this.courseRepository = courseRepository;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void enroll(EnrollCourseServiceDto dto) {
        validateBeforeEnroll(dto.userId(), dto.courseId());

        Course course = getCourseBy(dto.courseId());

        paymentService.pay(
                new PaymentDto(
                        dto.userId(),
                        dto.courseId(),
                        BigDecimal.valueOf(course.getPrice()),
                        dto.paymentMethod()
                )
        );

        enrolledCourseRepository.save(dto.toEntity());
    }

    public List<Course> getEnrolledCourses(Long userId) {
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findEnrolledCoursesByUserId(userId);

        return courseRepository.findByIdsAndPublished(
                enrolledCourses.stream().map(EnrolledCourse::getCourseId).toList()
        );
    }

    private void validateBeforeEnroll(Long userId, Long courseId) {
        validateAlreadyEnrolled(userId, courseId);

        User foundUser = getUserBy(userId);
        Course foundCourse = getCourseBy(courseId);

        validateCourseBelongsToUser(foundUser, foundCourse);
    }

    private void validateAlreadyEnrolled(Long userId, Long courseId) {
        if (enrolledCourseRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw BusinessException.builder(ExceptionCode.ALREADY_ENROLLED).withId(courseId).build();
        }
    }

    private Course getCourseBy(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.COURSE_NOT_FOUND).build());
    }

    private User getUserBy(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.USER_NOT_FOUND).withId(userId).build());
    }

    private void validateCourseBelongsToUser(User user, Course course) {
        if (isOwner(user.getUserId(), course.getInstructorId())) {
            throw BusinessException.builder(ExceptionCode.COURSE_BELONGS_TO_USER).withId(
                    user.getUserId(), course.getId()
            ).build();
        }
    }

    private boolean isOwner(Long userId, Long courseId) {
        return Objects.equals(userId, courseId);
    }
}
