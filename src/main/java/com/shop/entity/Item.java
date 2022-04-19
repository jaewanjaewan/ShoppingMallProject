package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.shop.exception.OutOfStockException;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{ //상품 정보를 저장하는 엔티티

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

    //상품을 수정했을때 엔티티를 업데이트하는 메소드
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    //상품을 주문할 경우 상품의 재고를 감소시키는 메소드
    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber; //상품의 재고 수량에서 주문 후 남은 재고 수량을 구함
        if(restStock<0){ //상품의 재고가 주문 수량보다 작을 경우 재고 부족 예외를 발생시킴
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock; //주문 후 남은 재고 수량을 상품의 현재 재고 값으로 할당
    }

    //상품의 재고를 증가시키는 메소드(주문 취소 했을떄)
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }

}
