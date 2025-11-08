package com.example.projectlxp.service.lecture;

import com.example.projectlxp.tmp.LectureNotFoundException;
import com.example.projectlxp.tmp.SectionNotFoundException;
import com.example.projectlxp.model.lecture.Lecture;
import com.example.projectlxp.model.lecture.LectureType;
import com.example.projectlxp.model.section.Section;
import com.example.projectlxp.repository.lecture.LectureRepository;
import com.example.projectlxp.repository.section.SectionRepository;
import com.example.projectlxp.service.lecture.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LectureServiceImpl implements LectureService{

    private final LectureRepository lectureRepository;
    private final SectionRepository sectionRepository;

    public LectureServiceImpl(LectureRepository lectureRepository, SectionRepository sectionRepository) {
        this.lectureRepository = lectureRepository;
        this.sectionRepository = sectionRepository;
    }

    // 렉처 생성
    @Transactional
    public LectureResponseDTO createLecture(LectureCreateRequestDTO requestDTO) {
        Section section = sectionRepository.findByIdAndNotDeleted(requestDTO.getSectionId())
                .orElseThrow(() -> new SectionNotFoundException(requestDTO.getSectionId()));
        //requestDTO 검증
        validateLectureDurationAndPath(requestDTO);

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
        return LectureResponseDTO.from(savedLecture);
    }

    // 렉처 조회 (ID)
    public LectureResponseDTO getLectureById(Long id) {
        Lecture lecture = lectureRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new LectureNotFoundException(id));
        return LectureResponseDTO.from(lecture);
    }

    // 특정 섹션의 모든 렉처 조회
    public List<LectureResponseDTO> getLecturesBySection(Long sectionId) {
        sectionRepository.findByIdAndNotDeleted(sectionId)
                .orElseThrow(() -> new SectionNotFoundException(sectionId));

        return lectureRepository.findBySectionIdAndNotDeletedOrderByOrder(sectionId).stream()
                .map(LectureResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 미리보기 가능한 렉처 조회
    public List<LectureResponseDTO> getPreviewableLecturesBySection(Long sectionId) {
        sectionRepository.findByIdAndNotDeleted(sectionId)
                .orElseThrow(() -> new SectionNotFoundException(sectionId));

        return lectureRepository.findPreviewableLecturesBySectionId(sectionId).stream()
                .map(LectureResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 특정 타입의 렉처 조회
    public List<LectureResponseDTO> getLecturesBySectionAndType(Long sectionId, LectureType type) {
        sectionRepository.findByIdAndNotDeleted(sectionId)
                .orElseThrow(() -> new SectionNotFoundException(sectionId));

        return lectureRepository.findBySectionIdAndTypeAndNotDeletedOrderByOrder(sectionId, type).stream()
                .map(LectureResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 특정 코스의 모든 렉처 조회
    public List<LectureResponseDTO> getLecturesByCourse(Long courseId) {
        return lectureRepository.findAllByCourseIdAndNotDeleted(courseId).stream()
                .map(LectureResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 렉처 수정
    @Transactional
    public LectureResponseDTO updateLecture(Long id, LectureUpdateRequestDTO requestDTO) {
        Lecture lecture = lectureRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new LectureNotFoundException(id));

        lecture.updateDetails(requestDTO);

        Lecture updatedLecture = lectureRepository.save(lecture);
        return LectureResponseDTO.from(updatedLecture);
    }

    // 렉처 삭제 (Soft Delete)
    @Transactional
    public void deleteLecture(Long id) {
        Lecture lecture = lectureRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new LectureNotFoundException(id));

        lecture.softDelete();
        lectureRepository.save(lecture);
    }

    // 렉처 순서 변경
    @Transactional
    public void reorderLectures(Long sectionId, List<Long> lectureIds) {
        sectionRepository.findByIdAndNotDeleted(sectionId)
                .orElseThrow(() -> new SectionNotFoundException(sectionId));

        List<Lecture> lectures = lectureRepository.findAllByIdInAndDeletedAtIsNull(lectureIds);

        Map<Long, Lecture> lectureMap = lectures.stream()
                .collect(Collectors.toMap(Lecture::getId, lecture -> lecture));

        for (int i = 0; i < lectureIds.size(); i++) {
            Long lectureId = lectureIds.get(i);
            Lecture lecture = lectureMap.get(lectureId);

            if (lecture == null) {
                throw new LectureNotFoundException(lectureId);
                continue;
            }
            lecture.updateOrder(i+1);
        }
    }

}
