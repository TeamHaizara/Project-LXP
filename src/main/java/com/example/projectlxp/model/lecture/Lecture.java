package com.example.projectlxp.model.lecture;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.service.lecture.dto.LectureUpdateRequestDTO;
import com.example.projectlxp.model.section.Section;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import static com.example.projectlxp.model.lecture.exception.LectureExceptionCode.LECTURE_ALREADY_DELETED;
import static com.example.projectlxp.model.lecture.exception.LectureExceptionCode.ORDER_NUMBER_UNDER_ZERO;


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
    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureType type;

    @Column(name = "resource_path", nullable = false, columnDefinition = "TEXT")
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

    public Lecture(Section section, String title, String description, Integer sortOrder, LectureType type, String resourcePath, Integer duration, Boolean isPreviewable) {
        this.section = section;
        this.title = title;
        this.description = description;
        this.sortOrder = sortOrder;
        this.type = type;
        this.resourcePath = resourcePath;
        this.duration = duration;
        this.isPreviewable = isPreviewable;
    }

    public static Lecture forCreate(Section section, String title, String description, Integer order, LectureType type,String resourcePath, Integer duration, Boolean isPreviewable) {

        return new Lecture(section, title, description, order, type, resourcePath, duration, isPreviewable);
    }

    // Soft delete method
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void updateDetails(LectureUpdateRequestDTO requestDTO) {
        validateDeleted();

        if (requestDTO.getTitle() != null) {
            this.title = requestDTO.getTitle();
        }
        if (requestDTO.getDescription() != null) {
            this.description = requestDTO.getDescription();
        }
        if(requestDTO.getOrder() != null) {
            updateOrder(requestDTO.getOrder());
        }
        if (requestDTO.getType() != null) {
            this.type = requestDTO.getType();
        }
        if (requestDTO.getResourcePath() != null) {
            this.resourcePath = requestDTO.getResourcePath();
        }
        if (requestDTO.getDuration() != null) {
            this.duration = requestDTO.getDuration();
        }
        if (requestDTO.getIsPreviewable() != null) {
            this.isPreviewable = requestDTO.getIsPreviewable();
        }
    }

    public void updateOrder(Integer order) {
        validateOrder(order);
        validateDeleted();

        setSortOrder(order);
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

    public boolean isDeleted() {
        return deletedAt != null;
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

    protected void setSection(Section section) {
        this.section = section;
    }

    public String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    protected void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LectureType getType() {
        return type;
    }

    protected void setType(LectureType type) {
        this.type = type;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public Integer getDuration() {
        return duration;
    }

    protected void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Boolean getIsPreviewable() {
        return isPreviewable;
    }

    protected void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    protected void setPreviewable(Boolean previewable) {
        isPreviewable = previewable;
    }

    protected void setIsPreviewable(Boolean isPreviewable) {
        this.isPreviewable = isPreviewable;
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
                ", isPreviewable=" + isPreviewable +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }

}
