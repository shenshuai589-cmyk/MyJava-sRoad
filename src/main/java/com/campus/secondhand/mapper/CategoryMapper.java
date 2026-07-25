package com.campus.secondhand.mapper;

import com.campus.secondhand.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 查询所有分类
     */
    List<Category> findAll();

    Category findById(Long id);
}
