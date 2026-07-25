package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.dto.LoginDTO;
import com.campus.secondhand.dto.PasswordUpdateDTO;
import com.campus.secondhand.dto.RegisterDTO;
import com.campus.secondhand.dto.UserUpdateDTO;
import com.campus.secondhand.pojo.User;
import com.campus.secondhand.service.UserService;
import com.campus.secondhand.utils.UserContext;
import com.campus.secondhand.vo.LoginVO;
import com.campus.secondhand.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    @Operation(summary = "查询用户列表")
    public Result<List<UserVO>> list() {
        List<User> users = userService.findAll();

        List<UserVO> vos = users.stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);

            return vo;
        })
                .toList();
        return Result.success(vos);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<UserVO> register(@RequestBody @Valid RegisterDTO registerDTO) {
        UserVO userVO = userService.register(registerDTO);

        return Result.success(userVO);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {

        LoginVO loginVO = userService.login(loginDTO);

        return Result.success(loginVO);
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前登录用户")
    public Result<UserVO> info() {
        Long userId = UserContext.getUserId();

        UserVO userVO = userService.getUserInfo(userId);

        return Result.success(userVO);
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户信息")
    public Result<Void> update(@RequestBody @Valid UserUpdateDTO dto) {

        userService.update(dto);
        return Result.success();
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        userService.updatePassword(dto);

        return Result.success();
    }
}
