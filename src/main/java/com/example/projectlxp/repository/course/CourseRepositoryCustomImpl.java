package com.example.projectlxp.repository.course;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.service.course.dto.CourseSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CourseRepositoryCustomImpl implements CourseRepositoryCustom {

    private final EntityManager entityManager;

    public CourseRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Course> searchByCriteria(CourseSearchCriteria criteria) {
        StringBuilder jpql = new StringBuilder("SELECT c FROM Course c WHERE c.status = 'PUBLISHED'");
        Map<String, Object> params = new HashMap<>();

        if (criteria.getInstructorId() != null) {
            jpql.append(" AND c.instructorId = :instructorId");
            params.put("instructorId", criteria.getInstructorId());
        }

        if (criteria.getCategoryId() != null) {
            jpql.append(" AND c.categoryId = :categoryId");
            params.put("categoryId", criteria.getCategoryId());
        }

        if (criteria.getKeyword() != null) {
            jpql.append(" AND c.title LIKE :keyword");
            params.put("keyword", "%" + criteria.getKeyword() + "%");
        }

        TypedQuery<Course> query = entityManager.createQuery(jpql.toString(), Course.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }
}
