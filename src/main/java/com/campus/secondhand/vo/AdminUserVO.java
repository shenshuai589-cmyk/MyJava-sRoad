package com.campus.secondhand.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserVO {

    /**
     * 用户id
     */
    private Long id;

    /**
     *  用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色
     */
    private Integer role;

    /**
     * 状态
     */
    private Integer status;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
