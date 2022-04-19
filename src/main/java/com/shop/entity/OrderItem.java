package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class OrderItem extends BaseEntity{ //주문한 상품의 정보를 저장하는 엔티티

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    //사용하지 않는 데이터도 한꺼번에 조회하는 즉시로딩을 이용하면 성능문제를 일으켜 실무에서는 사용하기 힘들다
    @ManyToOne(fetch = FetchType.LAZY) //지연로딩방식
    @JoinColumn(name = "item_id")
    private Item item; //하나의 상품은 여러 주문 상품으로 들어갈 수 있으므로 주문 상품 기준으로 다대일 단방향 매핑 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; //한번의 주문에 여러 개의 상품을 주문할 수 있으므로 주문 상품 엔티티와 주문 엔티티를 다대일 단방향 매핑 설정

    private int orderPrice; //주문가격
    private int count; //수량

    //주문할 상품과 주문 수량을 통해 OrderItem 객체를 만드는 메소드
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice()); //상품 가격을 주문 가격으로 세팅
        item.removeStock(count); //주문 수량 만큼 상품의 재고 수량을 감소
        return orderItem;
    }

    //주문 가격과 주문 수량을 곱해서 해당 상품을 추문한 총 가격을 계산하는 메소드
    public int getTotalPrice(){
        return orderPrice*count;
    }

    //주문 취소시 주문 수량만큼 상품의 재고를 더해줌
    public void cancel() {
        this.getItem().addStock(count);
    }

}
