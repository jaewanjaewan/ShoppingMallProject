package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name="cart_item")
public class CartItem extends BaseEntity{ //장바구니에 담기는 아이템 정보를 저장하는 엔티티

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    //사용하지 않는 데이터도 한꺼번에 조회하는 즉시로딩(EAGER)을 이용하면 성능문제를 일으켜 실무에서는 사용하기 힘들다
    @ManyToOne(fetch = FetchType.LAZY) //지연로딩(LAZY) 방식 설정
    @JoinColumn(name="cart_id")
    private Cart cart; //하나의 장바구니에는 여러 개의 상품을 담을 수 있으므로 다대일관계 매핑

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; //하나의 상품은 여러 장바구니의 상품으로 담길 수 있으므로 다대일 관계 매핑

    private int count; //같은 상품을 장바구니에 몇 개 담을지 지정

    //장바구니에 담을 상품 엔티티를 생성하는 메소드
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    //장바구니에 기존에 담겨 있는 상품인데, 해당 상품을 추가로 장바구니에 담을 때 기존 수량에 현재 담을 수량을 더해줄때 사용할 메소드
    public void addCount(int count){
        this.count += count;
    }

    //현재 장바구니에 담겨있는 수량을 변경하는 메소드
    public void updateCount(int count){
        this.count = count;
    }

}
