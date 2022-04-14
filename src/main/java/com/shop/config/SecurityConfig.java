package com.shop.config;

import com.shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity //이 어노테이션을 선언하면 SpringSecurityFilterChain이 자동으로 포함됨
public class SecurityConfig extends WebSecurityConfigurerAdapter { //상속 받아서 메소드 오버라이딩을 통해 보안 설정을 커스터마이징 할 수 있음

    @Autowired
    MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception { //http 요청에 대한 보안을 설정
        http.formLogin()
                .loginPage("/members/login") //로그인 페이지 URL 설정
                .defaultSuccessUrl("/") //로그인 성공 시 이동할 URL 설정
                .usernameParameter("email") //로그인 시 사용할 파라미터 이름으로 email을 지정
                .failureUrl("/members/login/error") //로그인 실패 시 이동할 URL 설정
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) //로그아웃 URL 설정
                .logoutSuccessUrl("/"); //로그아웃 성공 시 이동할 URL 설정
    }

    @Bean
    public PasswordEncoder passwordEncoder(){ //비밀번호를 암호화하여 저장
        return new BCryptPasswordEncoder();
    }

    /*Spring 시큐리티에서 인증은 AuthenticationManager를 통해 이루어지며 AuthenticationManagerBuilder가
    AuthenticationManager를 생성한다. userDetailsService를 구현하고 있는 객체로 memberService를 지정 해주머
    비밀번호 암호화를 위해 passwordEncoder를 지정*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
    }
}
