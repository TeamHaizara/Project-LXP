package com.example.projectlxp.model.lecture;

import com.example.projectlxp.model.lecture.exception.LectureAlreadyDeletedException;
import com.example.projectlxp.model.lecture.exception.LectureExceptionCode;
import com.example.projectlxp.model.lecture.exception.LectureOrderBoundException;
import com.example.projectlxp.service.lecture.dto.LectureUpdateRequestDTO;
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

    public Lecture(Section section, String title, String description, Integer order, LectureType type, String resourcePath, Integer duration, Boolean isPreviewable) {
        this.section = section;
        this.title = title;
        this.description = description;
        this.order = order;
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
        if (requestDTO.getTitle() != null) {
            this.title = requestDTO.getTitle();
        }
        if (requestDTO.getDescription() != null) {
            this.description = requestDTO.getDescription();
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
        if (order < 0) {
            throw new LectureOrderBoundException();
        }

        if (this.deletedAt != null) {
            throw new LectureAlreadyDeletedException();
        }
        setOrder(order);
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

    public Integer getOrder() {
        return order;
    }

    protected void setOrder(Integer order) {
        this.order = order;
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
                ", order=" + order +
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
