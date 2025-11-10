package com.example.projectlxp.controller.section;

import com.example.projectlxp.controller.section.request.SectionCreateRequest;
import com.example.projectlxp.controller.section.request.SectionUpdateRequest;
import com.example.projectlxp.controller.section.response.SectionResponse;
import com.example.projectlxp.service.section.SectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/courses/{courseId}/sections")
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long courseId,
            @Valid @RequestBody SectionCreateRequest request
    ) {
        SectionResponse response = sectionService.createSection(request.toDto(courseId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/sections/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long sectionId,
            @Valid @RequestBody SectionUpdateRequest request
    ) {
        SectionResponse response = sectionService.updateSection(sectionId, request.toDto());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long sectionId) {
        sectionService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/courses/{courseId}/sections/reorder")
    public ResponseEntity<Void> reorderSections(
            @PathVariable Long courseId,
            @RequestBody List<Long> sectionIds
    ) {
        sectionService.reorderSections(courseId, sectionIds);
        return ResponseEntity.ok().build();
    }
}
