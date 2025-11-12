package com.example.projectlxp.model.category;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    protected Category() {
    }

    // 생성자의 접근 제어자를 private으로 변경하여 외부에서의 직접 생성을 막습니다.
    private Category(String name) {
        this.name = name;
    }

    // 엔티티 생성을 책임지는 정적 팩토리 메소드를 제공합니다.
    public static Category create(String name) {
        return new Category(name);
    }

    // 엔티티의 상태를 변경하는 비즈니스 메소드
    public void update(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
