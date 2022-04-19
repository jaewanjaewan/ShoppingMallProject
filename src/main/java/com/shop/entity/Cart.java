package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString
public class Cart extends BaseEntity{ //장바구니 정보를 저장하는 엔티티

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //사용하지 않는 데이터도 한꺼번에 조회하는 즉시로딩(EAGER)을 이용하면 성능문제를 일으켜 실무에서는 사용하기 힘들다
    @OneToOne(fetch = FetchType.LAZY) //회원 엔티티와 일대일 매핑, 지연로딩(LAZY) 방식 설정
    @JoinColumn(name="member_id") //매핑할 외래키 지정, name 속성에는 매핑할 외래키의 이름을 설정
    private Member member;

    //회원 엔티티를 파라미터로 받아서 장바구니 엔티티를 생성
    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }

}
