package com.example.projectlxp.service.cart;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.cart.Cart;
import com.example.projectlxp.repository.cart.CartRepository;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.enroll.EnrolledCourseRepository;
import com.example.projectlxp.repository.user.UserRepository;
import com.example.projectlxp.service.cart.dto.CartServiceDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    CartRepository cartRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CourseRepository courseRepository;
    @Mock
    EnrolledCourseRepository enrolledCourseRepository;

    @InjectMocks
    CartServiceImpl cartService;

    private final Long userId = 1L;
    private final Long courseId = 10L;

    @Test
    void addCart_정상_등록() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(enrolledCourseRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(false);

        cartService.addCart(new CartServiceDto(userId, courseId));

        verify(cartRepository).save(isA(Cart.class));
    }

    @Test
    void addCart_이미수강중이면_예외() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(enrolledCourseRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(true);

        assertThatThrownBy(() -> cartService.addCart(new CartServiceDto(userId, courseId)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ExceptionCode.ALREADY_ENROLLED.getMessage());
    }

    @Test
    void getAllCarts_삭제되지않은_카트만반환() {
        Cart active = Cart.of(userId, courseId);
        Cart deleted = Cart.of(userId, 20L);
        ReflectionTestUtils.invokeMethod(deleted, "softDelete");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(cartRepository.findAllByUserIdAndDeletedAtIsNull(userId))
                .thenReturn(List.of(active));

        List<Cart> carts = cartService.getAllCartItems(userId).toList();

        assertThat(carts).containsExactly(active);
        verify(cartRepository).findAllByUserIdAndDeletedAtIsNull(userId);
    }

    @Test
    void deleteCart_softDelete_수행() {
        Cart cart = Cart.of(userId, courseId);

        //when(userRepository.existsById(userId)).thenReturn(true);
        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(cartRepository.findByUserIdAndCourseIdAndDeletedAtIsNull(userId, courseId))
                .thenReturn(Optional.of(cart));

        cartService.deleteCart(new CartServiceDto(userId, courseId));

        assertThat(cart.getDeletedAt()).isNotNull();
    }

    @Test
    void deleteAllCart_모든항목_softDelete() {
        Cart first = Cart.of(userId, courseId);
        Cart second = Cart.of(userId, 20L);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(cartRepository.findAllByUserIdAndDeletedAtIsNull(userId))
                .thenReturn(List.of(first, second));
        when(courseRepository.existsById(anyLong())).thenReturn(true);

        cartService.deleteAllCart(userId);

        assertThat(first.getDeletedAt()).isNotNull();
        assertThat(second.getDeletedAt()).isNotNull();
    }
}
