package com.campus.secondhand.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductQueryDTO {
    // 页码
    private Integer page = 1;

    // 每页数量
    private Integer size = 10;

    // 商品关键字
    private String keyword;

    // 分类id
    private Long categoryId;

    // 最低价格
    private BigDecimal minPrice;

    // 最高价格
    private BigDecimal maxPrice;
}
