package com.shop.entity;

import com.shop.dto.MemberFormDto;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class CartTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    //회원 엔티티를 생성하는 메소드
    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest(){
        Member member = createMember();
        memberRepository.save(member);
        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        em.flush(); //회원, 장바구니 엔티티를 영속성 컨텍스트에 저장 후 em으로부터 flush()를 호출하여 db에 반영
        em.clear(); //실제 db에서 장바구니 엔티티를 가지고 올 때 회원 엔티티도 같이 가지고 오는지 보기 위해 영속성 컨텍스트를 비움

        Cart savedCart = cartRepository.findById(cart.getId()) //저장된 장바구니 엔티티를 조회
                .orElseThrow(EntityNotFoundException::new);

        //member 엔티티 id와 savedCart에 매핑된 member 엔티티의 id를 비교
        assertEquals(savedCart.getMember().getId(), member.getId());
    }
}
