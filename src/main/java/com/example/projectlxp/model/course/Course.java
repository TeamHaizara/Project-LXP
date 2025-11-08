package com.example.projectlxp.model.course;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.model.lecture.Lecture;
import com.example.projectlxp.model.section.Section;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.projectlxp.exception.ExceptionCode.*;

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

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
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

    // Soft delete
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void cascadeSoftDelete() {
        this.softDelete();
        this.sections.forEach(section -> {
            section.softDelete();
            section.getLectures().forEach(Lecture::softDelete);
        });
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

    // PUBLISHED로 상태 전이
    public void toPublished() {
        transitionTo(CourseStatus.PUBLISHED);
    }

    // ARCHIVED로 상태 전이
    public void toArchived() {
        transitionTo(CourseStatus.ARCHIVED);
    }

    // DELETED로 상태 전이 (enrolled user == 0)
    public void toDeleted(int enrolledUserCount) {
        if (enrolledUserCount > 0) {
            throw new BusinessException(COURSE_HAS_ENROLLED_STUDENTS, (long) enrolledUserCount);
        }
        transitionTo(CourseStatus.DELETED);
    }

    // 상태 전이 메서드
    private void transitionTo(CourseStatus newStatus) {
        validateStatusTransition(newStatus);
        this.status = newStatus;
    }

    // 상태 전이 규칙 검증 (도메인 규칙만 확인, 사전 조건은 미포함)
    private void validateStatusTransition(CourseStatus newStatus) {
        if (newStatus == null) {
            throw new BusinessException(COURSE_STATUS_NULL);
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new BusinessException(INVALID_COURSE_STATUS_TRANSITION, this.status.name(), newStatus.name());
        }
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
