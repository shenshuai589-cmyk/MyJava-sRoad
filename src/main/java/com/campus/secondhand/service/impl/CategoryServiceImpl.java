package com.campus.secondhand.service.impl;

import com.campus.secondhand.mapper.CategoryMapper;
import com.campus.secondhand.pojo.Category;
import com.campus.secondhand.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;
    @Override
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }
}
