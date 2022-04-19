package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

//Member 엔티티를 db에 저장하기 위한 인터페이스
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email); //회원가입시 중복된 회원이 있는지 검사하기 위해 이메일로 회원을 검사

}
