package com.campus.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FavoriteVO {

    /**
     * 收藏id
     */
    private Long id;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 卖家id
     */
    private Long sellerId;

    /**
     * 卖家名称
     */
    private String sellerName;

    /**
     * 收藏时间
     */
    private LocalDateTime createTime;

}
