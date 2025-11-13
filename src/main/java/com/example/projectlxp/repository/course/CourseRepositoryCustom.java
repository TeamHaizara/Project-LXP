package com.example.projectlxp.repository.course;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.service.course.dto.CourseSearchCriteria;

import java.util.List;

public interface CourseRepositoryCustom {
    List<Course> searchByCriteria(CourseSearchCriteria criteria);
}
