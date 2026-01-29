package com.ch.jwtserver.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberRequest {

    private String homepageId;
    private String password;
    private String name;

}