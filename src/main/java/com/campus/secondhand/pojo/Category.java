package com.campus.secondhand.pojo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Category {

    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类
     * 0表示一级分类
     */
    private Long parentId;

    /**
     * 排序
     */
    private Integer sort;

    private LocalDateTime createTime;
}
