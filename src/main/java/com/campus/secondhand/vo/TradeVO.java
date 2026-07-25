package com.campus.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeVO {
    /**
     * 交易id
     */
    private Long id;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品标题
     */
    private String productTitle;

    /**
     * 商品图片
     */
    private String productImage;


    /**
     * 成交价格
     */
    private BigDecimal price;

    /**
     * 买家名字
     */
    private String buyerName;

    /**
     * 卖家名字
     */
    private String sellerName;

    /**
     * 交易状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;






}
