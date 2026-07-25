package com.campus.secondhand.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewVO {

    /**
     * 评价人id
     */
    private Long id;

    /**
     * 评分人名称
     */
    private String username;
    /**
     * 评价人头像
     */
    private String avatar;
    /**
     * 分数
     */
    private Integer score;

    /**
     * 内容
     */
    private String content;
    /**
     * 标签
     */
    private String tags;

    private LocalDateTime createTime;
}
