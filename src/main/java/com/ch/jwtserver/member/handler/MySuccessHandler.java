package com.ch.jwtserver.member.handler;

import com.ch.jwtserver.member.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MySuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.debug("MySuccessHandler 성공");

        // 인증된 사용자 정보 가져오기
        String homepageId = authentication.getName();

        // JWT 토큰 생성
        String token = jwtTokenProvider.createAccessToken(homepageId, List.of("ROLE_USER"));
        log.debug("token is {} in MySuccessHandler", token);

        // JSON 응답 설정
        response.setContentType("application/json;charset=UTF-8");

        // Map 형태로 전송
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("ok", true);
        responseData.put("accessToken", token);
        responseData.put("tokenType", "Bearer");
        responseData.put("name", homepageId);

        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }
}