package com.shop.repository;

import com.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
    //이미지가 잘 저장됐는지 테스트 하기위한 메소드
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

    //구매 이력 페이지에서 주문 상품의 대표 이미지를 보여주기 위한 메소드
    ItemImg findByItemIdAndRepimgYn(Long itemId, String repimgYn);
}
