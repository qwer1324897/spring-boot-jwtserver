package com.ch.jwtserver.member.service;

import com.ch.jwtserver.member.dto.MemberRequest;
import com.ch.jwtserver.member.entity.Member;
import com.ch.jwtserver.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 가입시키기
    @Transactional
    public Member regist(MemberRequest memberRequest) {

        Member member = new Member();
        member.setHomepageId(memberRequest.getHomepageId());
        member.setPassword(passwordEncoder.encode(memberRequest.getPassword()));
        member.setName(memberRequest.getName());

        return memberRepository.save(member);
    }

    // 회원 1명 가져오기
    @Transactional(readOnly = true)  // JPA 는 SELECT 라 할지라도 트랜잭션을 붙인다.
    public Member findByHomepageId(String homepageId) {

        return memberRepository.findByHomepageId(homepageId).orElseThrow(()-> new IllegalArgumentException("존재하지 않은 회원입니다."));
    }

    // PasswordEncoder 를 이용하여 클라이언트가 입력한 암호화되지 않은 raw password 와 암호화된 db의 비번을 비교하기
    public boolean matchPassword(String rawPassword, Member member) {
        return passwordEncoder.matches(rawPassword, member.getPassword());
    }
}