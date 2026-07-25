package com.campus.secondhand.vo;

import lombok.Data;

@Data
public class LoginVO {
    /**
     * jwt令牌
     */
    private String token;

    /**
     * 用户信息
     */
    private UserVO user;
}
