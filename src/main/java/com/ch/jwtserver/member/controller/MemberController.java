package com.ch.jwtserver.member.controller;

import com.ch.jwtserver.member.dto.LoginResponse;
import com.ch.jwtserver.member.dto.MemberRequest;
import com.ch.jwtserver.member.dto.MemberResponse;
import com.ch.jwtserver.member.entity.Member;
import com.ch.jwtserver.member.jwt.JwtTokenProvider;
import com.ch.jwtserver.member.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class MemberController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/test")
    public String getMessage() {
        return "하위";
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody MemberRequest memberRequest) throws IllegalAccessException {
        // 원래는 스프링 시큐리리가 알아서 회원정보 검증을 수행하지만, 지금의 경우 JWT 토큰 발급만 구현해보기 위해 formLogin 비활성화를 시켜놓았으니,
        // 직접 회원정보를 가져와서 비밀번호를 검증하자.
        // 참고로 formLogin.disable() 했다고 해서 시큐리리의 멀티체인이 동작하지 않는 것은 아니다.
        // formLogin.disable() 은 UsernamePasswordAuthenticationFilter 를 통한 요청 흐름을 안 타겠다는 의미로,
        // 개발자가 필터체인 중 원하는 시점에 참여하는 코드를 작성하면 여전히 필터체인이 동작한다.
        // 가령 AuthenticationManager 의 호출을 개발자가 직접 해버리면 이후 요청 처리가 동작될 수 있다.

        // 회원정보 가져와서
        Member member = authService.findByHomepageId(memberRequest.getHomepageId());
        // 지금 member 에는 DB에서 가져온 암호화된 비밀번호가 들어가 있다. 따라서 클라이언트가 입력한 비밀번호도 같이 암호화한 다음 비교한다.

        // 비밀번호 검증을 처리
        if (!authService.matchPassword(memberRequest.getPassword(), member)) {
            log.debug("비밀번호 불일치");
            throw new IllegalAccessException("로그인 정보가 올바르지 않습니다.");
        }
        log.debug("\n\n 회원 인증 성공");

        // 로그인에 성공한 유저에게는 응답 정보에 JWT 토큰을 적재하여 보내자
        String token = jwtTokenProvider.createAccessToken(member.getHomepageId(), List.of("ROLE_USER"));  // (회원아이디, 스프링이 제공하는 권한)
        log.debug("\n\n token 은: {}", token);

        return new LoginResponse(token, "Bearer");  // Bearer: Http 통신 시 헤더에 넣을 수 있는 공식적인 key 값.(헤더값) 인증 토큰 등을 전송 시 그 형식을 의미.
    }

    // 연습용 회원의 암호 생성 요청을 처리
    @PostMapping
    public Object regist(@RequestBody MemberRequest memberRequest) { // String homepageId, String password 로 하면 클라이언트에서 json 으로 날라와서 못 받으니까
        // 객체화 시켜서 받아야 Jackson이 json 이랑 매핑을 해주니까, 클래스를 따로 만들어서 객체화 한 다음 받고, 앞에 @RequestBody 를 붙여서 Jackson이 인식하게 함
        // 서비스 객체.regsit()
        Member member = authService.regist(memberRequest);

//      return member; 이렇게 보낼 수 있음. 문법상 문제 안됨. 그러나
        // Member 엔터리를 응답 정보나 요청 정보에 return 해버리면
        // Member 에 있는 Password 같은 민감한 정보가 json 문자열로 클라이언트에게 그대로 전송되어버림...
        // 따라서 Member entity는 요청, 응답에 직접 사용해선 안되며, 반드시 요청과 응답에 필요한 필드만 따로 DTO로 정의해서 사용한다.

        // 응답에 필요한 데이터 구성하기
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setHomepageId(member.getHomepageId());
        memberResponse.setName(member.getName());

        return memberResponse;
    }
}