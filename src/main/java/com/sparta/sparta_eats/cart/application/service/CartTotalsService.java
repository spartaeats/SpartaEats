package com.sparta.sparta_eats.cart.application.service;

import com.sparta.sparta_eats.cart.domain.entity.Cart;
import com.sparta.sparta_eats.cart.infrastructure.repository.CartRepository;
import com.sparta.sparta_eats.cart.presentation.dto.response.CartTotalsResponse;
import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.user.domain.entity.User;
import com.sparta.sparta_eats.user.domain.entity.UserRole;
import com.sparta.sparta_eats.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 장바구니 총액 계산 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartTotalsService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    /**
     * 장바구니 총액 계산 조회
     * 
     * @param userId 사용자 ID
     * @param cartId 장바구니 ID
     * @return 장바구니 총액 정보
     */
    public CartTotalsResponse getCartTotals(String userId, UUID cartId) {
        // 1. 사용자 조회 및 권한 확인
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        
        if (user.getRole() != UserRole.CUSTOMER) {
            throw new IllegalArgumentException("CUSTOMER 권한이 필요합니다.");
        }

        // 2. 장바구니 조회 (본인 소유 확인)
        Cart cart = cartRepository.findWithItemsByIdAndUser(cartId, user)
                .orElseThrow(() -> new NotFoundException("장바구니를 찾을 수 없습니다."));

        // 3. 총액 계산 및 응답 생성
        return CartTotalsResponse.from(cart);
    }
}
