package com.campus.secondhand.vo;

import lombok.Data;

@Data
public class UserVO {

    private Long id;

    private String username;

    private String phone;

    private String avatar;

    private Integer creditScore;

    private Integer role;

}
