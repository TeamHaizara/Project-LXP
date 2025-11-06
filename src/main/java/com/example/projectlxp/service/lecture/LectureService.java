package com.example.projectlxp.service.lecture;

import com.example.projectlxp.Exception.LectureNotFoundException;
import com.example.projectlxp.Exception.SectionNotFoundException;
import com.example.projectlxp.model.lecture.Lecture;
import com.example.projectlxp.model.lecture.LectureType;
import com.example.projectlxp.model.section.Section;
import com.example.projectlxp.repository.lecture.LectureRepository;
import com.example.projectlxp.repository.section.SectionRepository;
import com.example.projectlxp.service.lecture.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LectureService {

    private final LectureRepository lectureRepository;
    private final SectionRepository sectionRepository;

    public LectureService(LectureRepository lectureRepository, SectionRepository sectionRepository) {
        this.lectureRepository = lectureRepository;
        this.sectionRepository = sectionRepository;
    }

    // 렉처 생성
    @Transactional
    public LectureResponseDTO createLecture(LectureCreateRequestDTO requestDTO) {
        Section section = sectionRepository.findByIdAndNotDeleted(requestDTO.getSectionId())
                .orElseThrow(() -> new SectionNotFoundException(requestDTO.getSectionId()));

        // order가 지정되지 않으면 자동으로 마지막에 추가
        Integer order = requestDTO.getOrder();
        if (order == null) {
            order = lectureRepository.findMaxOrderBySectionId(requestDTO.getSectionId())
                    .orElse(0) + 1;
        }

        Lecture lecture = new Lecture(
                section,
                requestDTO.getTitle(),
                requestDTO.getDescription(),
                order,
                requestDTO.getType()
        );
        lecture.setResourcePath(requestDTO.getResourcePath());
        lecture.setDuration(requestDTO.getDuration());
        lecture.setIsPreviewable(requestDTO.getIsPreviewable() != null ? requestDTO.getIsPreviewable() : false);

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

        if (requestDTO.getTitle() != null) {
            lecture.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getDescription() != null) {
            lecture.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getOrder() != null) {
            lecture.setOrder(requestDTO.getOrder());
        }
        if (requestDTO.getType() != null) {
            lecture.setType(requestDTO.getType());
        }
        if (requestDTO.getResourcePath() != null) {
            lecture.setResourcePath(requestDTO.getResourcePath());
        }
        if (requestDTO.getDuration() != null) {
            lecture.setDuration(requestDTO.getDuration());
        }
        if (requestDTO.getIsPreviewable() != null) {
            lecture.setIsPreviewable(requestDTO.getIsPreviewable());
        }

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

        for (int i = 0; i < lectureIds.size(); i++) {
            Long lectureId = lectureIds.get(i);
            Lecture lecture = lectureRepository.findByIdAndNotDeleted(lectureId)
                    .orElseThrow(() -> new LectureNotFoundException(lectureId));
            
            lecture.setOrder(i + 1);
            lectureRepository.save(lecture);
        }
    }
}
