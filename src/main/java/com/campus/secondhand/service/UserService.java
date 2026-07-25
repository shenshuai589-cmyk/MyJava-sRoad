package com.campus.secondhand.service;

import com.campus.secondhand.dto.LoginDTO;
import com.campus.secondhand.dto.PasswordUpdateDTO;
import com.campus.secondhand.dto.RegisterDTO;
import com.campus.secondhand.dto.UserUpdateDTO;
import com.campus.secondhand.pojo.User;
import com.campus.secondhand.vo.LoginVO;
import com.campus.secondhand.vo.UserVO;

import java.util.List;

public interface UserService {
    List<User> findAll();

    UserVO register(RegisterDTO  registerDTO);

    LoginVO login(LoginDTO loginDTO);

    UserVO getUserInfo(Long userId);

    void update(UserUpdateDTO dto);

    void updatePassword(PasswordUpdateDTO dto);


}
