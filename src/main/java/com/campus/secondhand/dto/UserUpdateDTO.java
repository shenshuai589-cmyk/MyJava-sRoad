package com.campus.secondhand.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;
}
