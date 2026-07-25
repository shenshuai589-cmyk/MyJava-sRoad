package com.campus.secondhand.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {

    private Long id;

    /**
     * 商品分类id
     */
    private Long categoryId;

    /**
     * 发布者id
     */
    private Long sellerId;

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


    private LocalDateTime updateTime;


}
