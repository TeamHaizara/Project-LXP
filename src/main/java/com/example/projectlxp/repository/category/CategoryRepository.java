package com.example.projectlxp.repository.category;

import com.example.projectlxp.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 카테고리 이름으로 존재 여부 확인 (중복 생성 방지용)
    boolean existsByName(String name);
}
