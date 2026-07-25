package com.campus.secondhand.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Review {

    private Long id;

    private Long tradeId;

    /**
     * 评价人
     */
    private Long fromUserId;

    /**
     * 被评价人
     */
    private Long toUserId;

    private Integer score;

    private String content;

    private String tags;

    private LocalDateTime createTime;
}
