package com.example.projectlxp.service.lecture;

import com.example.projectlxp.controller.lecture.request.LectureCreateRequest;
import com.example.projectlxp.controller.lecture.request.LectureUpdateRequest;
import com.example.projectlxp.controller.lecture.response.LectureListResponse;
import com.example.projectlxp.controller.lecture.response.LectureResponse;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.lecture.Lecture;
import com.example.projectlxp.model.lecture.LectureType;
import com.example.projectlxp.model.section.Section;
import com.example.projectlxp.repository.lecture.LectureRepository;
import com.example.projectlxp.repository.section.SectionRepository;
import com.example.projectlxp.service.lecture.exception.LectureServiceErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;
    private final SectionRepository sectionRepository;

    public LectureServiceImpl(LectureRepository lectureRepository, SectionRepository sectionRepository) {
        this.lectureRepository = lectureRepository;
        this.sectionRepository = sectionRepository;
    }

    // 렉처 생성
    @Override
    @Transactional
    public LectureResponse createLecture(Long courseId, Long sectionId, LectureCreateRequest requestDTO, Long userId) {
        Section section = sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND)
                        .withId(sectionId)
                        .build()
                );

        // sectionId가 courseId에 속하는지 검증
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
    public LectureResponse getLectureById(Long lectureId) {
        Lecture lecture = lectureRepository.findByIdAndDeletedAtIsNull(lectureId)
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.LECTURE_NOT_FOUND).withId(lectureId).build());
        return LectureResponse.from(lecture);
    }

    // 특정 섹션의 모든 렉처 조회
    public LectureListResponse getLecturesBySection(Long sectionId) {
        sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND).withId(sectionId).build());

        return new LectureListResponse(lectureRepository.findBySectionIdAsc(sectionId).stream()
                .map(LectureResponse::from)
                .collect(Collectors.toList()));
    }

    // 미리보기 가능한 렉처 조회
    public LectureListResponse getPreviewableLecturesBySection(Long sectionId) {
        sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND).withId(sectionId).build());

        return new LectureListResponse(lectureRepository.findBySectionIdPreviewableTrueAsc(sectionId).stream()
                .map(LectureResponse::from)
                .collect(Collectors.toList()));
    }

    // 특정 타입의 렉처 조회
    public LectureListResponse getLecturesBySectionAndType(Long sectionId, LectureType type) {
        sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND).withId(sectionId).build());

        return new LectureListResponse(lectureRepository.findBySectionIdAndTypeAsc(sectionId, type).stream()
                .map(LectureResponse::from)
                .collect(Collectors.toList()));
    }

    // 렉처 수정
    @Override
    @Transactional
    public LectureResponse updateLecture(Long courseId, Long sectionId, Long lectureId, LectureUpdateRequest requestDTO, Long userId) {
        Lecture lecture = findLectureWithRelations(lectureId);
        validateInstructorAccess(lecture, courseId, userId);
        validateLectureHierarchy(lecture, courseId, sectionId);

        lecture.updateDetails(requestDTO);

        Lecture updatedLecture = lectureRepository.save(lecture);
        return LectureResponse.from(updatedLecture);
    }

    // 렉처 삭제 (Soft Delete)
    @Override
    @Transactional
    public void deleteLecture(Long courseId, Long sectionId, Long lectureId, Long userId) {
        Lecture lecture = findLectureWithRelations(lectureId);
        validateInstructorAccess(lecture, courseId, userId);
        validateLectureHierarchy(lecture, courseId, sectionId);

        lecture.softDelete();
        lectureRepository.save(lecture);
    }

    // 렉처 순서 변경
    @Override
    @Transactional
    public void reorderLectures(Long courseId, Long sectionId, List<Long> lectureIds, Long userId) {
        Section section = sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND).withId(sectionId).build());

        // sectionId가 courseId에 속하는지 검증
        validateSectionBelongsToCourse(section, courseId);

        List<Lecture> lectures = lectureRepository.findAllByIdInAndStatusIsFalse(lectureIds);

        Map<Long, Lecture> lectureMap = lectures.stream()
                .collect(Collectors.toMap(Lecture::getId, lecture -> lecture));

        IntStream.range(0, lectureIds.size())
                .forEach(i -> {
                    Long lectureId = lectureIds.get(i);
                    Lecture lecture = lectureMap.get(lectureId);

                    validateReorderLecture(sectionId, lecture, lectureId);
                });
    }

    private Lecture findLectureWithRelations(Long lectureId) {
        return lectureRepository.findByIdWithSectionAndCourse(lectureId)
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.LECTURE_NOT_FOUND)
                        .withId(lectureId)
                        .build());
    }

    private void validateInstructorAccess(Lecture lecture, Long courseId, Long userId) {
        Course course = lecture.getSection().getCourse();
        if (!course.isOwnedBy(userId)) {
            throw BusinessException.builder(ExceptionCode.NOT_COURSE_INSTRUCTOR)
                    .withId(userId, courseId)
                    .build();
        }
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

    private void validateReorderLecture(Long sectionId, Lecture lecture, Long lectureId) {
        if (lecture == null) {
            throw BusinessException.builder(LectureServiceErrorCode.LECTURE_NOT_FOUND).withId(lectureId).build();
        }

        if (!Objects.equals(lecture.getSection().getId(), sectionId)) {
            throw BusinessException.builder(LectureServiceErrorCode.LECTURE_NOT_INCLUDED_SECTION).withId(lecture.getId(), sectionId)
                    .build();
        }
    }


}
