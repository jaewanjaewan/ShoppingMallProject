package com.shop.entity;

import com.shop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Auditing 테스트")
    //스프링 시큐리티에서 제공하는 어노테이션으로 어노테이션에 지정한 사용자가 로그인한 상태라고 가정하고 테스트를 진행할 수 있음
    @WithMockUser(username = "gildong", roles = "USER")
    public void auditingTest(){
        Member newMember = new Member();
        memberRepository.save(newMember);

        em.flush();
        em.clear();

        Member member = memberRepository.findById(newMember.getId())
                .orElseThrow(EntityNotFoundException::new);

        //member 엔티티를 저장할 때 등록자나 등록일을 지정해주지 않았지만 저장 시간과 현재 로그인된 계정의 이름으로 저장된 것을 확인할 수 있음
        System.out.println("register time : " + member.getRegTime());
        System.out.println("update time : " + member.getUpdateTime());
        System.out.println("create member : " + member.getCreatedBy());
        System.out.println("modify member : " + member.getModifiedBy());
    }
}
