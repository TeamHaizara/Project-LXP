package com.example.projectlxp.model.course;

import com.example.projectlxp.model.section.Section;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "courses", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

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
    protected Course() {
    }

    public Course(Long instructorId, Long categoryId, String title, String description, Integer price) {
        this.instructorId = instructorId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.price = price != null ? price : 0;
    }

    // Soft delete method
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    // Business logic methods
    public void updateBasicInfo(String title, String description, Integer price, Long categoryId) {
        if (title != null) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
        if (price != null) {
            this.price = price;
        }
        if (categoryId != null) {
            this.categoryId = categoryId;
        }
    }

    public void changeStatus(CourseStatus newStatus) {
        validateStatusTransition(newStatus);
        this.status = newStatus;
    }

    public void publish() {
        changeStatus(CourseStatus.PUBLISHED);
    }

    public void archive() {
        changeStatus(CourseStatus.ARCHIVED);
    }

    private void validateStatusTransition(CourseStatus newStatus) {
        if (this.status == CourseStatus.DELETED) {
            throw new IllegalStateException("삭제된 코스의 상태는 변경할 수 없습니다.");
        }
        // 추가 검증 로직이 필요하면 여기에 구현
        // DRAFT -> PUBLISHED, ARCHIVED 가능
        // PUBLISHED -> ARCHIVED 가능
        // ARCHIVED -> PUBLISHED 가능 (재개설)
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPrice() {
        return price;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public List<Section> getSections() {
        return sections;
    }

    // Helper methods
    public void addSection(Section section) {
        sections.add(section);
        section.setCourse(this);
    }

    public void removeSection(Section section) {
        sections.remove(section);
        section.setCourse(null);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", instructorId=" + instructorId +
                ", categoryId=" + categoryId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", sections=" + sections +
                '}';
    }

}
