package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional //로직을 처리하다가 에러가 발생하면, 변경된 데이터를 로직을 수행하기 이전 상태로 콜백시켜줌
@RequiredArgsConstructor //final이나 @NonNull이 붙은 필드에 생성자를 생성해줌
public class MemberService implements UserDetailsService { //UserDetailsService인터페이스는 db에서 회원 정보를 가져오는 역할 담당

    //빈을 주입하는 방법에는 @Autowired, 필드주입, 생성자주입이 있다.
    private final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    //이미 가입된 회원의 경우 IllegalStateException 예외를 발생 시킴
    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    //UserDetailsService 인터페이스에 있는 메소드이며 회원정보를 조회하여 사용자의 정보와 권한을 갖는 UserDetails 인터페이스 반환
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }

        //UserDetail을 구현하고 있는 User 객체를 반환
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

}
