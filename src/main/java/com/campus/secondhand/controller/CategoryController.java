package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.mapper.CategoryMapper;
import com.campus.secondhand.pojo.Category;
import com.campus.secondhand.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@Tag(name = "商品分类管理")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    @Operation(summary = "查询商品分类")
    public Result<List<Category>> list() {
        List<Category> list = categoryService.findAll();

        return Result.success(list);
    }


}
