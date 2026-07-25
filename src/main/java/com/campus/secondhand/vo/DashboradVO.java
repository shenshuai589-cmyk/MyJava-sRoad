package com.campus.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboradVO {

    /**
     * 用户数量
     */
    private Integer userCount;


    /**
     * 商品数量
     */
    private Integer productCount;

    /**
     * 订单数量
     */
    private Integer tradeCount;

    /**
     * 成交金额
     */
    private BigDecimal tradeAmount;
}
