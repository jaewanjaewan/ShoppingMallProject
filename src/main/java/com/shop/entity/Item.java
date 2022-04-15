package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO) //키 값을 생성하는 전략 명시
    private Long id; //상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(name = "price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob //BLOB(바이너리 데이터를 db외부에 저장하기 위한 타입), CLOB(사이즈가 큰 데이터를 외부 파일로 저장하기 위한 데이터 타입) 타입 매핑
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING) //enum 타입 매핑
    private ItemSellStatus itemSellStatus; //상품 판매 상태

}
