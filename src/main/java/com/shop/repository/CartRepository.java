package com.shop.repository;

import com.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> { //장바구니를 조회하기 위한 인터페이스

    Cart findByMemberId(Long memberId); //현재 로그인한 회원의 Cart 엔티티를 찾기 위한 메소드

}
