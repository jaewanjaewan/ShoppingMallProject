package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity{
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    //사용하지 않는 데이터도 한꺼번에 조회하는 즉시로딩(EAGER)을 이용하면 성능문제를 일으켜 실무에서는 사용하기 힘들다
    @ManyToOne(fetch = FetchType.LAZY) //지연로딩(LAZY) 방식 설정
    @JoinColumn(name = "member_id")
    private Member member; //한명의 회원은 여러 번 주문을 할 수 있으므로 다대일 단방향 매핑

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태

    /*외래키(order_id)가 order_item 테이블에 있으므로 연관관계의 주인은 OrderItem 엔티티이다. Order 엔티티가 주인이 아니므로
    mappedBy속성으로 연관관계의 주인을 설정. 속성의 값으로 order를 적어준 이유는 OrderItem에 있는 order에 의해 관리된다는 의미로
     해석하면됨, cascade = CascadeType.ALL -> 부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이
     orphanRemoval = true -> 고아 객체 제거를 사용*/
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>(); //하나의 주문이 여러 개의 주문 상품을 갖으므로 List 자료형으로 매핑

    //orderItems에는 주문 상품 정보들을 담아준다.
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this); //Order 엔티티와 orderItem 엔티티가 양방향 참조 관계 이므로, orderItem 객체에도 order 객체를 세팅
    }

    //주문 객체를 만드는 메소드
    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member); //상품을 주문한 회원의 정보를 세팅

        //여러개의 주문상품을 담을 수 있도록 리스트형태로 파라미터 값을 받아 주문 객체에 orderItem 객체 추가
        for(OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER); //주문 상태를 ORDER로 세팅
        order.setOrderDate(LocalDateTime.now()); //현재 시간을 주문 시간으로 세팅
        return order;
    }

    //총 주문 금액을 구하는 메소드
    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    //주문 취소시 주문 상태를 취소 상태로 바꿔주는 메소드
    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

}
