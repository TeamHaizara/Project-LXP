package com.example.projectlxp.service.section;

import com.example.projectlxp.controller.section.response.SectionResponse;
import com.example.projectlxp.service.section.dto.SectionServiceDto;

import java.util.List;

public interface SectionService {
    SectionResponse createSection(SectionServiceDto sectionServiceDto, Long userId);

    SectionResponse updateSection(Long courseId, Long sectionId, SectionServiceDto sectionServiceDto, Long userId);

    void deleteSection(Long courseId, Long sectionId, Long userId);

    void reorderSections(Long courseId, List<Long> sectionIds, Long userId);
}
