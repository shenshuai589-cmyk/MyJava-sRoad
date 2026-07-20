package com.powernode.underworlds.service;

import com.powernode.underworlds.pojo.UnderworldStaff;
import java.util.List;

/**
 * 阴差业务逻辑层接口
 */
public interface UnderworldStaffService {

    /**
     * 录入新阴差
     */
    boolean saveStaff(UnderworldStaff staff);

    /**
     * 根据工号查询单个阴差
     */
    UnderworldStaff getStaffById(Long id);

    /**
     * 条件查询阴差列表
     */
    List<UnderworldStaff> getStaffList(Integer deptId, Byte status);

    /**
     * 修改阴差资料
     */
    boolean renewStaff(UnderworldStaff staff);

    /**
     * 根据工号除名（删除）
     */
    boolean removeStaff(Long id);
}