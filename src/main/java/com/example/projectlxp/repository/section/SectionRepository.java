package com.example.projectlxp.repository.section;

import com.example.projectlxp.model.section.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findByIdAndNotDeleted(Long sectionId);
}
