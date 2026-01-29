package com.ch.jwtserver.member.service;

import com.ch.jwtserver.member.dto.CustomUserDetails;
import com.ch.jwtserver.member.entity.Member;
import com.ch.jwtserver.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String homepageId) throws UsernameNotFoundException {
        Member member = memberRepository.findByHomepageId(homepageId).orElseThrow(()->new UsernameNotFoundException("존재하지 않는 회원입니다."));
        UserDetails userDetails = new CustomUserDetails(member);

        return userDetails;
    }
}
