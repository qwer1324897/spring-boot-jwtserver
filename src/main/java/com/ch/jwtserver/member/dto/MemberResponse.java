package com.ch.jwtserver.member.dto;

import lombok.Getter;
import lombok.Setter;

// Member 엔터리를 응답 정보나 요청 정보에 return 으로 줄 순 있으나, 응답정보에 사용해버리면
// Member 에 있는 Password 같은 민감한 정보가 json 문자열로 클라이언트에게 그대로 전송되어버림...
// 따라서 Member entity는 요청, 응답에 직접 사용해선 안되며, 반드시 요청과 응답에 필요한 필드만 따로 DTO로 정의해서 사용한다.
@Getter @Setter
public class MemberResponse {
    private Long memberId;
    private String homepageId;
    private String name;
}

