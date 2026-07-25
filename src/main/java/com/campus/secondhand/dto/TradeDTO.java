package com.campus.secondhand.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TradeDTO {
    /**
     * 商品id
     */
    @NotNull(message = "商品不能为空")
    private Long productId;

    /**
     * 交易方式
     */
    @NotNull(message = "交易方式不能为空")
    private Integer tradeType;

    /**
     * 交易时间
     */
    private LocalDateTime meetTime;

    /**
     * 交易地点
     */
    private String meetPlace;
}
