package com.example.projectlxp.service.section;

import com.example.projectlxp.controller.section.response.SectionResponse;
import com.example.projectlxp.service.section.dto.SectionServiceDto;

import java.util.List;

public interface SectionService {
    SectionResponse createSection(SectionServiceDto sectionServiceDto);

    SectionResponse updateSection(Long sectionId, SectionServiceDto sectionServiceDto);

    void deleteSection(Long sectionId);

    void reorderSections(Long courseId, List<Long> sectionIds);
}
