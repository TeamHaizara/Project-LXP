package com.example.projectlxp.model.course;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.section.Section;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long instructorId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
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
        this.price = price;
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

    public void publish() {
        transitionTo(CourseStatus.PUBLISHED);
    }

    public void archive() {
        transitionTo(CourseStatus.ARCHIVED);
    }

    /**
     * Performs a soft delete on this course and cascades the deletion to all associated sections and lectures.
     * <p>
     * 선요약: {@code @Transactional} 내에서만 호출하세요 안그럼 터짐
     * This method sets the {@code deletedAt} timestamp for the course, all its sections, and all lectures
     * within those sections. The course status is transitioned to {@code DELETED}.
     * <p>
     * <b>IMPORTANT:</b> This method accesses lazily-loaded collections ({@code sections}, {@code lectures}).
     * It <b>MUST</b> be called within an active transaction context to avoid {@code LazyInitializationException}.
     * <p>
     * <b>Recommended Usage:</b>
     * <ul>
     *   <li>Call via {@link com.example.projectlxp.service.course.CourseService#deleteCourse(Long)}
     *       which ensures proper transaction management and eager loading of associations.</li>
     *   <li>If calling directly, ensure the calling method is annotated with {@code @Transactional}
     *       and the course entity is loaded with its associations (e.g., using fetch join).</li>
     * </ul>
     *
     * @param enrolledUserCount the number of users currently enrolled in this course
     * @throws BusinessException                         if {@code enrolledUserCount > 0}, preventing deletion of courses with active enrollments
     * @throws org.hibernate.LazyInitializationException if called outside an active transaction context
     *                                                   or if the sections/lectures collections are not initialized
     * @see com.example.projectlxp.service.course.CourseService#deleteCourse(Long)
     */
    public void delete(int enrolledUserCount) {
        if (enrolledUserCount > 0) {
            throw BusinessException.builder(ExceptionCode.COURSE_HAS_ENROLLED_STUDENTS)
                    .withCount(enrolledUserCount)
                    .build();
        }
        transitionTo(CourseStatus.DELETED);
        this.deletedAt = LocalDateTime.now();
        cascadeSoftDelete();
    }

    /**
     * Internal helper method to cascade soft delete to all associated sections and lectures.
     * <p>
     * This method is called internally by {@link #delete(int)} and should not be invoked directly.
     */
    private void cascadeSoftDelete() {
        this.sections.forEach(section -> {
            section.cascadeSoftDelete();
            // section 하위의 lecture들에 대한 cascade deletion은 section에 위임함
            // section.getLectures().forEach(Lecture::softDelete);

        });
    }

    private void transitionTo(CourseStatus newStatus) {
        validateStatusTransition(newStatus);
        this.status = newStatus;
    }

    // 상태 전이 규칙 검증 (도메인 규칙만 확인, 사전 조건 미포함)
    private void validateStatusTransition(CourseStatus newStatus) {
        if (newStatus == null) {
            throw BusinessException.builder(ExceptionCode.COURSE_STATUS_NULL)
                    .build();
        }
        if (!this.status.canTransitionTo(newStatus)) {
            throw BusinessException.builder(ExceptionCode.INVALID_COURSE_STATUS_TRANSITION)
                    .withStatus(this.status, newStatus)
                    .build();
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
