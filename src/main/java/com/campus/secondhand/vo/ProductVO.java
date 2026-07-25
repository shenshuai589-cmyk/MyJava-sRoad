package com.campus.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {

    private Long id;

    /**
     * 商品名称
     */
    private  String name;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 发布用户
     */
    private String username;
}
