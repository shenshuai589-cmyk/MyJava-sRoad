
### User用户模块

```sql
CREATE TABLE `user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '加密密码(BCrypt)',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `student_no` varchar(30) DEFAULT NULL COMMENT '学号',
  `college` varchar(50) DEFAULT NULL COMMENT '学院/院系',
  `campus_area` varchar(50) DEFAULT NULL COMMENT '校区/宿舍楼',
  `verify_image` varchar(255) DEFAULT NULL COMMENT '学生证/校园卡照片',
  `is_verified` tinyint NOT NULL DEFAULT '0' COMMENT '校园认证状态:0-未认证 1-审核中 2-已认证 3-已驳回',
  `verify_remark` varchar(255) DEFAULT NULL COMMENT '认证驳回原因',
  `credit_score` int NOT NULL DEFAULT '100' COMMENT '信用分',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT '角色:0-普通用户 1-管理员',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态:1-正常 0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表'
```


### UserMapper

```java
package com.campus.secondhand.mapper;  
  
import com.campus.secondhand.dto.PasswordUpdateDTO;  
import com.campus.secondhand.pojo.User;  
  
import org.apache.ibatis.annotations.Mapper;  
import org.apache.ibatis.annotations.Param;  
  
import java.util.List;  
  
@Mapper  
public interface UserMapper {  
  
    List<User> findAll();  // 查询所有  
  
    User findByUsername(String username);  // 根据账号查询用户信息  
  
    int insert(User user); // 新建用户信息  
  
    User findById(Long id); // 根据主键id查询查询用户信息  
  
    User findByPhone(String phone);  // 根基手机号码查询用户信息  
  
    int update(User user); //更新用户信息  
  
    int updatePassword(User  user); //更改密码  
  
    int updateStatus(@Param("id") Long id, @Param("status") Integer status); // 更新账户状态  
  
    Integer count(); //返回用户数量  
  
}
```



### 为什么用了 JWT 还要存 Redis？（面试核心必问）

JWT 本身是无状态（Stateless）的，服务端无法主动让已签发的 JWT 废弃（除非等到过期）。在项目中结合 Redis 存储，主要是为了实现以下能力：

1. **实现主动退出登录（注销）** 当用户点击“退出登录”时，只需执行 `redisTemplate.delete(RedisConstant.LOGIN_USER + token)`。拦截器每次校验时先查 Redis，查不到就说明已注销，拦截该请求。
    
2. **实现单设备登录 / 踢人下线（挤号）** 如果账号被异地登录，可以先清除之前的旧 Token 缓存，实现旧设备失效。
    
3. **双重安全防护** 既利用了 JWT 解析快速的特点，又利用了 Redis 可控状态的优势。



### Category 商品分类模块

```sql
CREATE TABLE `category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '父分类ID,0表示一级分类',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表'
```


```java
package com.campus.secondhand.pojo;  
  
  
import lombok.Data;  
  
import java.time.LocalDateTime;  
  
@Data  
public class Category { // 分类  
  
    private Long id;  
  
    /**  
     * 分类名称  
     */  
    private String name;  
  
    /**  
     * 父分类  
     * 0表示一级分类  
     */  
    private Long parentId;  
  
    /**  
     * 排序  
     */  
    private Integer sort;  
  
    private LocalDateTime createTime;  
}
```



### Product 产品模块

```sql
CREATE TABLE `product` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `category_id` bigint unsigned NOT NULL COMMENT '分类ID',
  `seller_id` bigint unsigned NOT NULL COMMENT '发布者(卖家)用户ID',
  `title` varchar(100) NOT NULL COMMENT '商品标题',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `condition_level` tinyint NOT NULL DEFAULT '1' COMMENT '成色:1-全新 2-九成新 3-八成新 4-明显使用痕迹',
  `trade_type` tinyint NOT NULL DEFAULT '0' COMMENT '交易方式:0-线下面交 1-同城邮寄 2-均可',
  `description` text COMMENT '商品详情',
  `main_image` varchar(255) DEFAULT NULL COMMENT '主图',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览量',
  `favorite_count` int NOT NULL DEFAULT '0' COMMENT '收藏量',
  `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态:0-待审核 1-审核通过 2-已驳回',
  `audit_remark` varchar(255) DEFAULT NULL COMMENT '审核驳回原因',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态:0-下架 1-在售 2-交易中 3-已售出',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除:0-未删除 1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_seller` (`seller_id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_status_audit` (`status`,`audit_status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表'
```


```java
package com.campus.secondhand.pojo;  
  
import lombok.Data;  
  
import java.math.BigDecimal;  
import java.time.LocalDateTime;  
  
@Data  
public class Product { //商品  
  
    private Long id; //商品id  
  
    /**     * 商品分类id  
     */    private Long categoryId;  
  
    /**  
     * 发布者id  
     */    private Long sellerId;  
  
    /**  
     * 商品标题  
     */  
    private String title;  
  
    /**  
     * 商品价格  
     */  
    private BigDecimal price;  
  
  
    /**  
     * 成色  
     * 1全新  
     * 2九成新  
     * 3八成新  
     * 4明显使用痕迹  
     */  
    private Integer conditionLevel;  
  
  
    /**  
     * 交易方式  
     * 0线下面交  
     * 1邮寄  
     * 2均可  
     */  
    private Integer tradeType;  
  
    /**  
     * 商品描述  
     */  
    private String description;  
  
    /**  
     * 主图  
     */  
    private String mainImage;  
  
    /**  
     * 浏览量  
     */  
    private Integer viewCount;  
  
    /**  
     * 收藏量  
     */  
    private Integer favoriteCount;  
  
    /**  
     * 审核状态  
     * 0待审核  
     * 1通过  
     * 2驳回  
     */  
    private Integer auditStatus;  
  
  
    // '审核驳回原因'  
    private String auditRemark;  
  
    /**  
     * 商品状态  
     * 0下架  
     * 1在售  
     * 2交易中  
     * 3已售出  
     */  
    private Integer status;  
  
    /**  
     * 逻辑删除  
     */  
    private Integer isDeleted;  
  
  
    private LocalDateTime createTime;  
  
  
    // '乐观锁版本号'  
    private Integer version;  
  
  
    private LocalDateTime updateTime;  
  
  
}
```

