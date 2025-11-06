package com.example.projectlxp.model.lecture;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.example.projectlxp.model.section.Section;

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
    public Lecture() {
    }

    public Lecture(Section section, String title, String description, Integer order, LectureType type) {
        this.section = section;
        this.title = title;
        this.description = description;
        this.order = order;
        this.type = type;
    }

    // Soft delete method
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public LectureType getType() {
        return type;
    }

    public void setType(LectureType type) {
        this.type = type;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Boolean getIsPreviewable() {
        return isPreviewable;
    }

    public void setIsPreviewable(Boolean isPreviewable) {
        this.isPreviewable = isPreviewable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
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
