package com.example.projectlxp.model.section;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.lecture.Lecture;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public class Section {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


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
