package com.ch.jwtserver.member.repository;


import com.ch.jwtserver.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 홈페이지 아이디를 이용해 정보를 가져오는 메서드는 기본으로 지원하지 않으므로 개발자가 직접 정의해야 한다.
    Optional<Member> findByHomepageId(String homepageId); // select * from member where homepage_id=? 를 대신하는 메서드명. 이 때 카멜케이스로 작성해야 JPA 가 인식함.
}
