package com.campus.secondhand.mapper;

import com.campus.secondhand.dto.PasswordUpdateDTO;
import com.campus.secondhand.pojo.User;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> findAll();

    User findByUsername(String username);

    int insert(User user);

    User findById(Long id);

    int update(User user);

    int updatePassword(User  user);

    int updateStatus(Long id,Integer status);

    Integer count();

}
