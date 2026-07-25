package com.campus.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDetailVO {
    /**
     * 商品id
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 成色
     */
    private Integer conditionLevel;
    /**
     * 交易信息
     */
    private Integer tradeType;

    /**
     * 商品状态
     */
    private Integer status;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 卖家信息
     */
    private UserVO seller;

    /**
     * 商品图片
     */
    private List<String> images;

    /**
     * 是否收藏
     */
    private Boolean favorite;

    /**
     * 收藏数量
     */
    private Integer favoriteCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
