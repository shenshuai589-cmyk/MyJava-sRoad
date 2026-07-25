package com.campus.secondhand.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {

    @NotBlank(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "商品标题不能为空")
    private String title;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01",message = "价格必须大于0")
    private BigDecimal price;

    /**
     * 成色
     */
    private Integer conditionLevel;

    /**
     * 交易方式
     */
    private Integer tradeType;

    /**
     * 描述
     */
    private String description;

    /**
     * 商品图片
     */
    private List<String> images;

    private Integer size;
}
