package com.campus.secondhand.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDTO {

    /**
     * @NotBlank: 确保用户输入的字符串既不能是 null，也不能是空字符串 ""，更不能全是空格 "   "
     */

    @NotBlank(message = "用户不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "手机号不能为空")
    private String phone;

}
