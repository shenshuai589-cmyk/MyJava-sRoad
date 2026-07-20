package com.powernode.underworlds.service.impl;

import com.powernode.underworlds.mapper.UnderworldStaffMapper;
import com.powernode.underworlds.pojo.UnderworldStaff;
import com.powernode.underworlds.service.UnderworldStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 阴差业务逻辑层实现类
 */
@Service // 极其重要：告诉Spring这是一个Service组件，自动注入到Spring容器中
public class UnderworldStaffServiceImpl implements UnderworldStaffService {

    @Autowired // 自动注入我们刚才写好的 Mapper 接口
    private UnderworldStaffMapper staffMapper;

    @Override
    public boolean saveStaff(UnderworldStaff staff) {
        // 企业级业务扩展点：后续可以在这里给密码进行 MD5/BCrypt 加密
        // Mapper返回的是受影响行数，大于0表示成功插入
        return staffMapper.insertStaff(staff) > 0;
    }

    @Override
    public UnderworldStaff getStaffById(Long id) {
        return staffMapper.selectById(id);
    }

    @Override
    public List<UnderworldStaff> getStaffList(Integer deptId, Byte status) {
        return staffMapper.selectList(deptId, status);
    }

    @Override
    public boolean renewStaff(UnderworldStaff staff) {
        return staffMapper.updateStaff(staff) > 0;
    }

    @Override
    public boolean removeStaff(Long id) {
        return staffMapper.deleteById(id) > 0;
    }
}