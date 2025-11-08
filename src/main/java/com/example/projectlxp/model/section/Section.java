package com.example.projectlxp.model.section;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.lecture.Lecture;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "order", nullable = false)
    private Integer order;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setCourse(Course course) {
        // TODO
        return;
    }

    public void softDelete() {
        // TODO
        return;
    }

    public List<Lecture> getLectures() {
        // TODO
        return null;
    }

    public boolean isDeleted() {
        // TODO
        return false;
    }

}
