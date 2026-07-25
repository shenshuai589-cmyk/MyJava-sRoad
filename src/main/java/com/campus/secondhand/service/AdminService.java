package com.campus.secondhand.service;

import com.campus.secondhand.pojo.Product;
import com.campus.secondhand.vo.AdminUserVO;
import com.campus.secondhand.vo.DashboradVO;

import java.util.List;

public interface AdminService {

    /**
     * 查询待审核商品
     */
    List<Product> auditList();


    /**
     * 审核商品
     */
    void audit(Long id,Integer status);

    /**
     * 用户列表
     */
    List<AdminUserVO> userList();


    /**
     * 修改用户状态
     */
    void updateUserStatus(Long id,Integer status);

    /**
     *数量统计
     */
    DashboradVO dashborad();
}
