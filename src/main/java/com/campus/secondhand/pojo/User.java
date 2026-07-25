package com.campus.secondhand.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private String studentNo;
    private String college;
    private String campusArea;
    private String verifyImage;
    private Integer isVerified;
    private String verifyRemark;
    private Integer creditScore;
    private Integer role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
