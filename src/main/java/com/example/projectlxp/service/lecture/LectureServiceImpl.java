package com.example.projectlxp.service.lecture;

import com.example.projectlxp.controller.lecture.request.LectureCreateRequest;
import com.example.projectlxp.controller.lecture.response.LectureListResponse;
import com.example.projectlxp.controller.lecture.response.LectureResponse;
import com.example.projectlxp.controller.lecture.request.LectureUpdateRequest;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.model.lecture.Lecture;
import com.example.projectlxp.model.lecture.LectureType;
import com.example.projectlxp.model.section.Section;
import com.example.projectlxp.repository.lecture.LectureRepository;
import com.example.projectlxp.repository.section.SectionRepository;
import com.example.projectlxp.service.lecture.exception.LectureServiceErrorCode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public LectureResponse createLecture(LectureCreateRequest requestDTO) {
        Section section = sectionRepository.findByIdAndDeletedAtIsNull(requestDTO.getSectionId())
                .orElseThrow(
                        () -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND).withId(requestDTO.getSectionId()).build());

        // order가 지정되지 않으면 자동으로 마지막에 추가
        Integer order = requestDTO.getOrder();
        if (order == null) {
            order = lectureRepository.findMaxOrderBySectionId(requestDTO.getSectionId())
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

    // 특정 코스의 모든 렉처 조회
    public LectureListResponse getLecturesByCourse(Long courseId) {
        return new LectureListResponse(lectureRepository.findBySectionCourseIdAsc(
                        courseId).stream()
                .map(LectureResponse::from)
                .collect(Collectors.toList()));
    }

    // 렉처 수정
    @Transactional
    public LectureResponse updateLecture(Long lectureId, LectureUpdateRequest requestDTO) {
        Lecture lecture = lectureRepository.findByIdAndDeletedAtIsNull(lectureId)
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.LECTURE_NOT_FOUND).withId(lectureId).build());

        lecture.updateDetails(requestDTO);

        Lecture updatedLecture = lectureRepository.save(lecture);
        return LectureResponse.from(updatedLecture);
    }

    // 렉처 삭제 (Soft Delete)
    @Transactional
    public void deleteLecture(Long lectureId) {
        Lecture lecture = lectureRepository.findByIdAndDeletedAtIsNull(lectureId)
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.LECTURE_NOT_FOUND).withId(lectureId).build());

        lecture.softDelete();
        lectureRepository.save(lecture);
    }

    //섹션 아이디 내의 렉처 CASCADE 삭제 처리
    @Transactional
    public void deleteLecturesBySection(Long sectionId) {
        sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND).withId(sectionId).build());

        List<Lecture> lectureListBySection = lectureRepository.findBySectionIdAsc(sectionId);

        if (lectureListBySection.isEmpty()) {
            return;
        }

        lectureListBySection.forEach(Lecture::softDelete);
        lectureRepository.saveAll(lectureListBySection);
    }

    // 렉처 순서 변경
    @Transactional
    public void reorderLectures(Long sectionId, List<Long> lectureIds) {
        sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(() -> BusinessException.builder(LectureServiceErrorCode.SECTION_NOT_FOUND).withId(sectionId).build());

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
