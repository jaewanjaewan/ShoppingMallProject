package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "member")
@ToString
public class Member extends BaseEntity{
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true) //동일한 값이 db에 들어올 수 없도록 속성 지정
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    //Member 엔티티를 생성하는 메소드
    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword()); //비밀번호 암호화
        member.setPassword(password);
        member.setRole(Role.USER);
        return member;
    }
}
