package com.campus.secondhand.service.impl;

import com.campus.secondhand.exception.BusinessException;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.mapper.TradeMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.pojo.Product;
import com.campus.secondhand.pojo.User;
import com.campus.secondhand.service.AdminService;
import com.campus.secondhand.utils.UserContext;
import com.campus.secondhand.vo.AdminUserVO;
import com.campus.secondhand.vo.DashboradVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TradeMapper tradeMapper;


    @Override
    public List<Product> auditList() {
        checkAdmin();

        return productMapper.findAuditList();
    }



    @Override
    public void audit(Long id, Integer status) {
        checkAdmin();

        Product product = productMapper.findById(id);

        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        //审核状态
        if (status != 1 && status != 2){
            throw new BusinessException("商品状态错误");
        }


        //修改审核状态
        productMapper.updateAuditStatus(id, status);


        //审核通过
        if (status == 1){
            productMapper.updateStatus(id,1);
        }


        // 审核失败
        if(status==2){

            productMapper.updateStatus(id, 0);

        }
    }

    @Override
    public List<AdminUserVO> userList() {
        checkAdmin();
        List<User> users = userMapper.findAll();

        return users.stream()
                .map(user ->{
                    AdminUserVO vo = new AdminUserVO();
                    vo.setId(user.getId());
                    vo.setUsername(user.getUsername());
                    vo.setPhone(user.getPhone());
                    vo.setAvatar(user.getAvatar());
                    vo.setRole(user.getRole());
                    vo.setStatus(user.getStatus());
                    vo.setCreateTime(user.getCreateTime());

                    return vo;
                })
                .toList();
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        checkAdmin();

        User user = userMapper.findById(id);


        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (status != 0 && status != 1){
            throw new BusinessException("状态错误");
        }
        userMapper.updateStatus(id, status);
    }

    @Override
    public DashboradVO dashborad() {
        checkAdmin();

        DashboradVO vo = new DashboradVO();

        //用户数量
        vo.setUserCount(userMapper.count());

        //商品数量
        vo.setUserCount(productMapper.count());

        //完成订单数量
        vo.setTradeCount(tradeMapper.countFinish());

        //成交金额
        vo.setTradeAmount(tradeMapper.sumAmount());

        return vo;
    }

    private void checkAdmin() {

        Long userId = UserContext.getUserId();

        User user = userMapper.findById(userId);

        if (user == null || user.getRole() != 1) {
            throw new BusinessException("无管理员权限");
        }
    }
}
