package com.example.projectlxp.model.lecture;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.model.section.Section;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import static com.example.projectlxp.model.lecture.exception.LectureExceptionCode.LECTURE_ALREADY_DELETED;
import static com.example.projectlxp.model.lecture.exception.LectureExceptionCode.ORDER_NUMBER_UNDER_ZERO;


@Entity
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String resourcePath;

    @Column
    private Integer duration;

    @Column(nullable = false)
    private boolean previewable = false;

    @Column(nullable = false)
    private boolean status = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    protected Lecture() {
    }

    public Lecture(Section section, String title, String description, Integer sortOrder, LectureType type, String resourcePath, Integer duration, Boolean previewable) {
        this.section = section;
        this.title = title;
        this.description = description;
        this.sortOrder = sortOrder;
        this.type = type;
        this.resourcePath = resourcePath;
        this.duration = duration;
        this.previewable = previewable;
    }

    public static Lecture forCreate(Section section, String title, String description, Integer order, LectureType type, String resourcePath, Integer duration, Boolean isPreviewable) {

        return new Lecture(section, title, description, order, type, resourcePath, duration, isPreviewable);
    }

    // Soft delete method
    public void softDelete() {
        this.status = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void updateDetails(String title, String description, Integer sortOrder, LectureType type, String resourcePath, Integer duration, Boolean isPreviewable) {
        validateDeleted();

        if (title != null) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
        if (sortOrder != null) {
            updateOrder(sortOrder);
        }
        if (type != null) {
            this.type = type;
        }
        if (resourcePath != null) {
            this.resourcePath = resourcePath;
        }
        if (duration != null) {
            this.duration = duration;
        }
        if (isPreviewable != null) {
            this.previewable = isPreviewable;
        }
    }

    public void updateOrder(Integer order) {
        validateOrder(order);
        validateDeleted();

        sortOrder = order;
    }

    private void validateOrder(Integer order) {
        if (order < 0) {
            throw BusinessException.builder(ORDER_NUMBER_UNDER_ZERO).build();
        }
    }

    private void validateDeleted() {
        if (this.deletedAt != null) {
            throw BusinessException.builder(LECTURE_ALREADY_DELETED).build();
        }
    }


    public boolean getStatus() {
        return status;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public Section getSection() {
        return section;
    }


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }


    public LectureType getType() {
        return type;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public Integer getDuration() {
        return duration;
    }

    public Boolean isPreviewable() {
        return previewable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Lecture{" +
                "id=" + id +
                ", section=" + section +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", order=" + sortOrder +
                ", type=" + type +
                ", resourcePath='" + resourcePath + '\'' +
                ", duration=" + duration +
                ", isPreviewable=" + previewable +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }

}
