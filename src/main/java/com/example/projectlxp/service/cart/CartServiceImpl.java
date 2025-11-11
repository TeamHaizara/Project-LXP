package com.example.projectlxp.service.cart;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.cart.Cart;
import com.example.projectlxp.repository.cart.CartRepository;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.enroll.EnrolledCourseRepository;
import com.example.projectlxp.repository.user.UserRepository;
import com.example.projectlxp.service.cart.dto.CartServiceDto;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrolledCourseRepository enrolledCourseRepository;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository,
                           CourseRepository courseRepository, EnrolledCourseRepository enrolledCourseRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrolledCourseRepository = enrolledCourseRepository;
    }

    @Override
    @Transactional
    public void addCart(CartServiceDto dto) {
        validateReferenceIntegrity(dto.userId(), dto.courseId());
        cartRepository.save(dto.toEntity());
    }

    @Override
    @Transactional
    public List<Cart> getAllCarts(Long userId) {
        validateUserReference(userId);
        return cartRepository.findAllByUserIdAndDeletedAtIsNull(userId);
    }

    @Override
    @Transactional
    public void deleteCart(CartServiceDto dto) {
        validateCourseReference(dto.courseId());
        List<Cart> cartList = getAllCarts(dto.userId());
        cartList.stream().filter(cart -> cart.getCartCourseId().equals(dto.courseId())).findFirst()
                .ifPresentOrElse(cart -> {
                    cart.softDelete();
                    cartRepository.save(cart);
                }, () -> {
                    throw BusinessException.builder(ExceptionCode.CART_NOT_FOUND).build();
                });
    }

    @Override
    @Transactional
    public void deleteAllCart(Long userId) {
        List<Cart> cartList = getAllCarts(userId);
        cartList.forEach(cart -> {
            validateCourseReference(cart.getCartCourseId());
            cart.softDelete();
            cartRepository.save(cart);
        });
    }

    private void validateReferenceIntegrity(Long userId, Long courseId) {
        validateUserReference(userId);
        validateCourseReference(courseId);
        validateAlreadyEnrolled(userId, courseId);
    }

    private void validateAlreadyEnrolled(Long userId, Long courseId) {
        if (enrolledCourseRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw BusinessException.builder(ExceptionCode.ALREADY_ENROLLED).build();
        }
    }

    private void validateCourseReference(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw BusinessException.builder(ExceptionCode.COURSE_NOT_FOUND).withId(courseId).build();
        }
    }

    private void validateUserReference(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw BusinessException.builder(ExceptionCode.USER_NOT_FOUND).withId(userId).build();
        }
    }
}
