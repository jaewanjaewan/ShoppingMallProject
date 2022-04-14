package com.shop.config;

import com.shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
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

        http.authorizeRequests() //시큐리티 처리에 HttpServletRequest를 이용한다는 것을 의미
                //permitAll()을 통해 모든 사용자가 인증(로그인)없이 해당 경로에 접근할 수 있도록 설정
                .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN") ///admin으로 시작하는 경로는 해당 계정이 ADMIN Role일 경우에만 접근 가능
                .anyRequest().authenticated(); //나머지 경로들은 모두 인증을 요구하도록 설정

        http.exceptionHandling() //인증되지 않은 사용자가 리소스에 접근하였을 때 수행되는 핸들러를 등록
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
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

    //static 디렉터리의 하위 파일은 인증을 무시하도록 설정
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }
}
