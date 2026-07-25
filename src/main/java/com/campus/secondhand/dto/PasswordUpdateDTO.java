package com.campus.secondhand.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordUpdateDTO {

    /**
     * 原密码
     */
    @NotNull(message = "原密码不能为空")
    private String oldPassword;

    /**
     * 新密码
      */
    @NotNull(message = "新密码不能为空")
    private String newPassword;
}
