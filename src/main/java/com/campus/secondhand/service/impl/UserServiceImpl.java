package com.campus.secondhand.service.impl;

import com.campus.secondhand.dto.LoginDTO;
import com.campus.secondhand.dto.PasswordUpdateDTO;
import com.campus.secondhand.dto.RegisterDTO;
import com.campus.secondhand.dto.UserUpdateDTO;
import com.campus.secondhand.exception.BusinessException;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.pojo.User;
import com.campus.secondhand.service.UserService;
import com.campus.secondhand.utils.JwtUtil;
import com.campus.secondhand.utils.UserContext;
import com.campus.secondhand.vo.LoginVO;
import com.campus.secondhand.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;


    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public UserVO register(RegisterDTO registerDTO) {
        // 1.查询用户名是否存在
        User existUser =
                userMapper.findByUsername(registerDTO.getUsername());
        if(existUser!=null){
            throw new BusinessException("用户已存在");
        }

        // 2.创建用户对象
        User user = new User();
        user.setUsername(registerDTO.getUsername());

        // 3.BCRypt加密密码
        user.setPassword(
                passwordEncoder.encode(registerDTO.getPassword())
        );
        user.setPhone(registerDTO.getPhone());
        // 4.保存
        userMapper.insert(user);

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setPhone(user.getPhone());
        userVO.setAvatar(user.getAvatar());
        userVO.setCreditScore(user.getCreditScore());
        userVO.setRole(user.getRole());

        return userVO;

    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = userMapper.findByUsername(loginDTO.getUsername());
        if(user==null){
            throw new BusinessException("用户名或密码错误");
        }

        // 走到这一步说明用户名已存在，校验密码
        boolean result = passwordEncoder.matches(loginDTO.getPassword(),user.getPassword());

        if(!result){
            throw new BusinessException("用户名或密码错误");
        }

        // 运行到这一步说明账户和密码全对应上了，那么就可以使用user里的id
        // 生成token
        String token = JwtUtil.createToken(user.getId());

        // 转换userVO

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setPhone(user.getPhone());
        userVO.setRole(user.getRole());

        // 封装返回
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(userVO);

        return loginVO;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        if(user==null){
            throw new BusinessException("用户不存在");
        }
        UserVO vo = new UserVO();

        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());

        return vo;
    }

    @Override
    public void update(UserUpdateDTO dto) {

        // 当前登录用户
        Long userId = UserContext.getUserId();

        User user = userMapper.findById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setUsername(dto.getNickname());

        user.setPhone(dto.getPhone());

        user.setAvatar(dto.getAvatar());

        userMapper.update(user);

    }

    @Override
    public void updatePassword(PasswordUpdateDTO dto) {

        Long userId = UserContext.getUserId();

        User user = userMapper.findById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 校验密码
        boolean result = passwordEncoder.matches(dto.getOldPassword(), user.getPassword());

        if(!result){
            throw new BusinessException("原密码错误");
        }

        // 加密新密码
        String newPassword = passwordEncoder.encode(dto.getNewPassword());

        user.setPassword(newPassword);

        userMapper.update(user);

    }
}
