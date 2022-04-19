package com.shop.repository;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom { //querydsl을 Spring Data Jpa와 함꼐 사용하기 위해 사용자 정의 인터페이스 작성

    //관리자 페이지에 보여줄 상품 리스트를 가져오는 메소드
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    //메인 페이지에 보여줄 상품 리스트를 가져오는 메소드
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

}
