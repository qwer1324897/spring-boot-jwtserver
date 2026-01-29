package com.ch.jwtserver.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "homepage_id", length = 30)
    private String homepageId;

    @Column(name = "password", length = 64, nullable = false)
    private String password;

    @Column(name = "open_id", length = 255, unique = true)
    private String openId;

    @Column(name = "email", length = 30)
    private String email;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "regdate")
    private LocalDateTime regdate;

    @Column(name = "updated")
    private LocalDateTime updated;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;
}
