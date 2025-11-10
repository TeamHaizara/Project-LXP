package com.example.projectlxp.model.section;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.lecture.Lecture;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "`order`", nullable = false)
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    protected Section() {
    }

    private Section(Course course, String title, Integer order) {
        this.course = course;
        this.title = title;
        this.order = order;
    }

    public static Section create(Course course, String title, Integer order) {
        return new Section(course, title, order);
    }

    public void update(String title, Integer order) {
        this.title = title;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getOrder() {
        return order;
    }

    public List<Lecture> getLectures() {
        return lectures;
    }

    public void cascadeSoftDelete() {
        this.lectures.forEach(Lecture::softDelete);
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
