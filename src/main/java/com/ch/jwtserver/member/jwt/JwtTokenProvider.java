package com.ch.jwtserver.member.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

// JWT 토큰 발급과 해석을 담당하는 클래스
@Component
public class JwtTokenProvider {

    private SecretKey signature;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret) {
        this.signature = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /*=====================================
        토큰 생성
    ======================================*/
    public String createAccessToken(String user, List<String> roles) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(60*60);

        return Jwts.builder()
                .subject(user)  // subject. 주제. 즉 무엇과 관련된 것인가 를 뜻하는 메서드(지금의 경우 회원과 관련된 것)
                .claim("roles","value")  // claim. 주장하다. 즉 어떠한 사실을 명시하는 것. 페이로드에 담을 사용자 정의 정보.
                // 서버에서 해당 사용자가 요청한 기능을 수행할 자격이 있는지 판단하는 용도로 사용.
                // 공식 표준 필드는 아니지만, 개발자가 서비스 운영에 필요한 데이터(권한, 등급 등)를 Key-Value 형태로 자유롭게 추가할 때 사용함. ROLE_USER 같은거
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .signWith(signature)  // 서명
                .compact();
    }

    /*=====================================
        토큰 해석
    ======================================*/
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signature)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}