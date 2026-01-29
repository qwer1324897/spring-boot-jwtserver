package com.ch.jwtserver.member.security;

import com.ch.jwtserver.member.handler.MySuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        //CORS 정책을 담는 설정(허용할 출처/메서드(GET,POST...)/헤더 등) 객체
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // 금지사항!!!!!!!!!!!!!! * 패턴금지
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*")); //혹시 보안을 더 강화할 일이 있다면, 헤더를 지정하는게 좋다
        config.setAllowCredentials(true);//만일 true로 주지 않으면, 브라우저가 쿠키를 보내지 않거나 응답을 막음
        config.setMaxAge(3600L); //3600초 동안을 동일 조건이라면 preflight 를 매번 하지 않음

        //허용할 URI패턴   우리의 경우   /api/**
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**",  config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, MySuccessHandler mySuccessHandler) throws Exception {
        // 스프링 부트에서 설정해 놓은 자체 FilterChain 을 내가 원하는 방식(form, oauth2 등)으로 바꿔서
        // 바뀐 securityFilterChain 을 반환하자

        httpSecurity.cors(httpSecurityCorsConfigurer -> {});    // 위에 설정한 Bean 을 사용
        httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());

        // 폼 로그인 끄기
//        httpSecurity.formLogin(formLoginConfigurer -> formLoginConfigurer.disable());
        // 스프링에서 기본 제공되는 로그인폼을 쓰진 않지만, 필터 체인 로직을 그대로 사용할 수 있도록 설정.
        httpSecurity.formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")   // 스프링이 지원하는 디폴트 로그인 요청 URL 을 사용하지 않고 개발자가 원하는 것으로 바꿀 수 있다.
                .usernameParameter("homepageId")    // 스프링에게 로그인 파라미터 중 ID 변수명을 알려줌
                .passwordParameter("password")  // 비밀번호 알려줌
                .successHandler(mySuccessHandler)
        );

        httpSecurity.httpBasic(httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer.disable());

        httpSecurity.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                .requestMatchers("/test").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
        );

        return httpSecurity.build();
    }
}
