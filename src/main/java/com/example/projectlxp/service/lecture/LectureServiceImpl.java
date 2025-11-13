package com.example.projectlxp.service.lecture;

import com.example.projectlxp.controller.lecture.request.LectureCreateRequest;
import com.example.projectlxp.controller.lecture.response.LectureListResponse;
import com.example.projectlxp.controller.lecture.response.LectureResponse;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.lecture.Lecture;
import com.example.projectlxp.model.lecture.LectureType;
import com.example.projectlxp.model.section.Section;
import com.example.projectlxp.repository.enroll.EnrolledCourseRepository;
import com.example.projectlxp.repository.lecture.LectureRepository;
import com.example.projectlxp.repository.section.SectionRepository;
import com.example.projectlxp.service.lecture.dto.LectureUpdateDto;
import com.example.projectlxp.service.lecture.exception.LectureServiceErrorCode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;
    private final SectionRepository sectionRepository;
    private final EnrolledCourseRepository enrolledCourseRepository;

    public LectureServiceImpl(LectureRepository lectureRepository, SectionRepository sectionRepository,
                              EnrolledCourseRepository enrolledCourseRepository) {
        this.lectureRepository = lectureRepository;
        this.sectionRepository = sectionRepository;
        this.enrolledCourseRepository = enrolledCourseRepository;
    }

    // 렉처 생성
    @Override
    @Transactional
    public LectureResponse createLecture(Long courseId, Long sectionId, LectureCreateRequest requestDTO, Long userId) {
        Section section = sectionRepository.findByIdWithCourse(sectionId) // FETCH JOIN
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND)
                        .withId(sectionId)
                        .build()
                );

        validateInstructorAccess(section, userId);
        validateSectionBelongsToCourse(section, courseId);

        // order가 지정되지 않으면 자동으로 마지막에 추가
        Integer order = requestDTO.getOrder();
        if (order == null) {
            order = lectureRepository.findMaxOrderBySectionId(sectionId)
                    .orElse(0) + 1;
        }

        Lecture lecture = Lecture.forCreate(
                section,
                requestDTO.getTitle(),
                requestDTO.getDescription(),
                order,
                requestDTO.getType(),
                requestDTO.getResourcePath(),
                requestDTO.getDuration(),
                requestDTO.getIsPreviewable() != null ? requestDTO.getIsPreviewable() : false
        );

        Lecture savedLecture = lectureRepository.save(lecture);
        return LectureResponse.from(savedLecture);
    }

    // 렉처 조회 (ID)
    public LectureResponse getLectureById(Long lectureId, Long userId) {
        Course course = lectureRepository.findCourseByLectureId(lectureId).orElseThrow(
                () -> BusinessException.builder(LectureServiceErrorCode.LECTURE_NOT_INCLUDED_COURSE).withId(lectureId)
                        .build());

        validateEnrolled(userId, course.getId());

        Lecture lecture = lectureRepository.findByIdAndDeletedAtIsNull(lectureId)
                .orElseThrow(
                        () -> BusinessException.builder(LectureServiceErrorCode.LECTURE_NOT_FOUND).withId(lectureId)
                                .build());

        return LectureResponse.from(lecture);
    }

    private void validateEnrolled(Long userId, Long courseId) {
        if (!enrolledCourseRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw BusinessException.builder(LectureServiceErrorCode.NOT_ENROLLED_COURSE).withId(userId, courseId).build();
        }
    }

    // 특정 섹션의 모든 렉처 조회
    public LectureListResponse getLecturesBySection(Long sectionId) {
        sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(
                        () -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND).withId(sectionId)
                                .build());

        return new LectureListResponse(lectureRepository.findBySectionIdAsc(sectionId).stream()
                .map(LectureResponse::from)
                .collect(Collectors.toList()));
    }

    // 미리보기 가능한 렉처 조회
    public LectureListResponse getPreviewableLecturesBySection(Long sectionId) {
        sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(
                        () -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND).withId(sectionId)
                                .build());

        return new LectureListResponse(lectureRepository.findBySectionIdPreviewableTrueAsc(sectionId).stream()
                .map(LectureResponse::from)
                .collect(Collectors.toList()));
    }

    // 특정 타입의 렉처 조회
    public LectureListResponse getLecturesBySectionAndType(Long sectionId, LectureType type) {
        sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(
                        () -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND).withId(sectionId)
                                .build());

        return new LectureListResponse(lectureRepository.findBySectionIdAndTypeAsc(sectionId, type).stream()
                .map(LectureResponse::from)
                .collect(Collectors.toList()));
    }

    // 렉처 수정
    @Override
    @Transactional
    public LectureResponse updateLecture(LectureUpdateDto updateDto) {
        Lecture lecture = findLectureWithRelations(updateDto.lectureId());
        validateInstructorAccess(lecture, updateDto.userId());
        validateLectureHierarchy(lecture, updateDto.courseId(), updateDto.sectionId());

        lecture.updateDetails(
                updateDto.requestDTO().getTitle(),
                updateDto.requestDTO().getDescription(),
                updateDto.requestDTO().getOrder(),
                updateDto.requestDTO().getType(),
                updateDto.requestDTO().getResourcePath(),
                updateDto.requestDTO().getDuration(),
                updateDto.requestDTO().getPreviewable()
        );

        Lecture updatedLecture = lectureRepository.save(lecture);
        return LectureResponse.from(updatedLecture);
    }

    // 렉처 삭제 (Soft Delete)
    @Override
    @Transactional
    public void deleteLecture(Long courseId, Long sectionId, Long lectureId, Long userId) {
        Lecture lecture = findLectureWithRelations(lectureId);
        validateInstructorAccess(lecture, userId);
        validateLectureHierarchy(lecture, courseId, sectionId);

        lecture.softDelete();
        lectureRepository.save(lecture);
    }

    // 렉처 순서 변경
    @Override
    @Transactional
    public void reorderLectures(Long courseId, Long sectionId, List<Long> lectureIds, Long userId) {
        Section section = sectionRepository.findByIdWithCourseAndLectures(sectionId) // FETCH JOIN
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND)
                        .withId(sectionId)
                        .build());

        validateInstructorAccess(section, userId);
        validateSectionBelongsToCourse(section, courseId);
        List<Lecture> lecturesInDb = section.getLectures();
        Map<Long, Lecture> lectureMap = lecturesInDb.stream()
                .collect(Collectors.toMap(Lecture::getId, Function.identity()));

        Set<Long> dbLectureIds = lectureMap.keySet();
        Set<Long> requestLectureIds = new HashSet<>(lectureIds);

        if (!dbLectureIds.equals(requestLectureIds)) {
            throw BusinessException.builder(ExceptionCode.INVALID_LECTURE_REORDER_REQUEST)
                    .build();
        }

        IntStream.range(0, lectureIds.size())
                .forEach(i -> {
                    Lecture lecture = lectureMap.get(lectureIds.get(i));
                    lecture.updateOrder(i + 1);
                });
    }

    private Lecture findLectureWithRelations(Long lectureId) {
        return lectureRepository.findByIdWithSectionAndCourse(lectureId) // FETCH JOIN
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.LECTURE_NOT_FOUND)
                        .withId(lectureId)
                        .build());
    }

    private void validateInstructorAccess(Course course, Long userId) {
        if (!course.isOwnedBy(userId)) {
            throw BusinessException.builder(ExceptionCode.NOT_COURSE_INSTRUCTOR)
                    .withId(userId, course.getId())
                    .build();
        }
    }

    private void validateInstructorAccess(Section section, Long userId) {
        Course course = section.getCourse();
        validateInstructorAccess(course, userId);
    }

    private void validateInstructorAccess(Lecture lecture, Long userId) {
        Course course = lecture.getSection().getCourse();
        validateInstructorAccess(course, userId);
    }

    private void validateLectureHierarchy(Lecture lecture, Long courseId, Long sectionId) {
        validateLectureBelongsToSection(lecture, sectionId);
        validateSectionBelongsToCourse(lecture.getSection(), courseId);
    }

    private void validateSectionBelongsToCourse(Section section, Long expectedCourseId) {
        if (!section.getCourse().getId().equals(expectedCourseId)) {
            throw BusinessException.builder(ExceptionCode.SECTION_NOT_IN_COURSE)
                    .withId(section.getId(), expectedCourseId)
                    .build();
        }
    }

    private void validateLectureBelongsToSection(Lecture lecture, Long expectedSectionId) {
        if (!lecture.getSection().getId().equals(expectedSectionId)) {
            throw BusinessException.builder(ExceptionCode.LECTURE_NOT_IN_SECTION)
                    .withId(lecture.getId(), expectedSectionId)
                    .build();
        }
    }


}
