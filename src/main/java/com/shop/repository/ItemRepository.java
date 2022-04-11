package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/*JpaRepository는 2개의 제네릭 타입을 사용하는데 첫 번째에는 엔티티 타입 클래스를 넣고, 두 반째는 기본키
타입을 넣어준다. JpaRepository는 기본적인 CRUD 및 페이징 처리를 위한 메소드가 정의돼 있다.*/
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByItemNm(String itemNm); //상품을 상품명으로 조회하는 쿼리 메소드
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail); //상품을 상품명과 상세 설명을 OR 조건을 이용하여 조회하는 쿼리 메소드
    List<Item> findByPriceLessThan(Integer price); //파라미터로 넘어온 price 변수보다 값이 작은 상품 데이터를 조회하는 쿼리 메소드
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price); //price 변수보다 값이 작은 상품 데이터를 가격이 높은 순으로 조회

    @Query("select i from  Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);
}
