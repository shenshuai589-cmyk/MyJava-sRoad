package com.campus.secondhand.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Trade {

    private Long id;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 买家
     */
    private Long buyerId;

    /**
     *卖家
     */
    private Long sellerId;


    /**
     * 成交价格
     */
    private BigDecimal price;

    /**
     * 交易方式
     */
    private Integer tradeType;

    /**
     * 面交时间
     */
    private LocalDateTime meetTime;

    /**
     * 面交地点
     */
    private String meetPlace;

    /**
     * 状态
     */
    private Integer status;

    private String cancelReason;

    private LocalDateTime createTime;

    private LocalDateTime finishTime;
}
