package com.example.projectlxp.repository.cart;

import com.example.projectlxp.model.cart.Cart;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    //일반 유저용 조회(deletedAt == null)
    public List<Cart> findAllByUserIdAndDeletedAtIsNull(Long userId);

    //삭제된 카트 복구용 전체 조회
    public List<Cart> findAllByUserId(Long userId);

    //개별 조회
    public Optional<Cart> findByUserIdAndCourseIdAndDeletedAtIsNull(Long userId, Long courseId);

    //장바구니 코스 중복여부 확인
    boolean existsByUserIdAndCourseIdAndDeletedAtIsNull(Long userId, Long courseId);
}
