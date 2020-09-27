package com.victolee.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDto {
    private String id;
    private String password;
    private String phone;
    private String email;
    private String address;
    private String role;
    private String carrer;
    private String u_name;
    private String token;
}