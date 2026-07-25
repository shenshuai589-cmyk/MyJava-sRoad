package com.campus.secondhand.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDTO {

    /**
     * 交易ID
     */

    @NotNull(message = "交易不能为空")
    private Long tradeId;


    /**
     * 被评分人
     */
//   @NotNull(message = "评价对象不能为空")
//    private Long toUserId;

    /**
     * 评分
     */
    @NotNull(message = "评分不能为空")
    @Min(value=1,message = "最低1分")
    @Max(value=5,message = "最高5分")
    private Integer score;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签
     */
    private String tags;
}
