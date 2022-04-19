package com.shop.repository;

import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import com.shop.dto.CartDetailDto;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{ //장바구니에 들어갈 상품을 저장하거나 조회하기 위한 인터페이스

    CartItem findByCartIdAndItemId(Long cartId, Long itemId); //카트 아이디와 상품 아이디를 이용해서 상품이 장바구니에 들어있는지 조회

    //장바구니에 페이지에 전달할 CartDetailDto 리스트를 쿼리 하나로 조회하는 JPQL문을 작성
    //성능 최적화가 필요할 경우 아래 코드와 같이 DTO 생성자를 이용하여 반환값으로 DTO 객체를 생성할 수 있음
    @Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " + //장바구니에 담겨있는 상품의 대표 이미지만 가지고 오도록 조건물을 작성
            "and im.repimgYn = 'Y' " +
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);

}
