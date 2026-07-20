package com.powernode.underworlds.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * 阴差员工实体类
 * 对应数据库表：underworld_staff
 */
@Data
public class UnderworldStaff implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工编号（主键自增）
     */
    private Long id;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 职位尊称(如:勾魂使者·白无常)
     */
    private String realName;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 状态(0:在职, 1:历劫中, 2:离职)
     */
    private Byte status;
}