package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

/*JpaRepository는 2개의 제네릭 타입을 사용하는데 첫 번째에는 엔티티 타입 클래스를 넣고, 두 반째는 기본키
타입을 넣어준다. JpaRepository는 기본적인 CRUD 및 페이징 처리를 위한 메소드가 정의돼 있다.*/
//상품을 여러가지 메소드로 조회하기위한 인터페이스
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {

    //상품을 상품명으로 조회하는 쿼리 메소드
    List<Item> findByItemNm(String itemNm);

    //상품을 상품명과 상세 설명을 OR 조건을 이용하여 조회하는 쿼리 메소드
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    //파라미터로 넘어온 price 변수보다 값이 작은 상품 데이터를 조회하는 쿼리 메소드
    List<Item> findByPriceLessThan(Integer price);

    //price 변수보다 값이 작은 상품 데이터를 가격이 높은 순으로 조회
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    //상품을 상세 설명으로 조회 하는 쿼리메소드
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc") //JPQL로 작성한 쿼리문(복잡한 쿼리의 경우 @Query 어노테이션을 사용하면 됨)
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail); //어노테이션을 이용하여 파라미터로 넘어온값을 JPQL에 들어갈 변수로 지정해줄 수 있음

    //기존의 db에서 사용하던 쿼리를 그대로 사용해야 할 때는 @Query의 nativeQuery속성을 사용하면 기존 쿼리를 그대로 활용할 수 있음
    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);

}
