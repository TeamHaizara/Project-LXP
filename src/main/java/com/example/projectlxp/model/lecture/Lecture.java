package com.example.projectlxp.model.lecture;

import com.example.projectlxp.model.section.Section;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lectures")
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "order", nullable = false)
    private Integer order;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private LectureType type;

    @Column(name = "resource_path", columnDefinition = "TEXT")
    private String resourcePath;

    @Column
    private Integer duration;

    @Column(name = "is_previewable", nullable = false)
    private Boolean isPreviewable = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void softDelete() {
        // TODO
        return;
    }
    
}
