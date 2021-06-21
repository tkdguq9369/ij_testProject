package com.kim.spring.springboot.config.auth;

import com.kim.spring.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity  // Spring security설정들을 활성화시켜줌.
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .csrf().disable()
                .headers().frameOptions().disable() //h2-console화면을 사용하기위해 해동 옵션을 disable한다.
                .and()
                    .authorizeRequests()            //URL별 권한관리를 설정하는 옵션의 시작. authorizeRequests가 선언되어야 antMatchers옵션 사용 가능
                    .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile", "/oauth2/**").permitAll()  //관리대상 지정 옵션. URL, HTTP메소드별로 관리 가능. 해당 라인은 전체열람 권한줌.
                    .antMatchers("/api/v1/**").hasRole(Role.USER.name())                                // "/api/v1/** " 주소를 가진 API는 USER권한을 가진 사람만 가능.
                    .anyRequest().authenticated()   //  anyRequest() 설정값 외 나머지 URL을 나타냄.    authenticated()를 추가하여 나머지 URL은 모두 인증된 사용자들에게만 허용. (로그인 사용자)
                .and()
                    .logout()
                        .logoutSuccessUrl("/")      //로그아웃 성공시 / 주소로 이동.
                .and()
                    .oauth2Login()                  // Oauth2 로그인 기능에 대한 여러 설정의 진입점
                        .userInfoEndpoint()         // Oauth2 로그인 성공 이후 사용자 정보를 가져올때의 설정 담당
                            .userService(customOAuth2UserService);      // 소셜 로그인 성공시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록.
                                                                        // 리소스 서버에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능 명시.
    }
}
