package com.campus.secondhand.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductImage {

    private Long id;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 排序
     */
    private Integer sort;

    private LocalDateTime createTime;



}
