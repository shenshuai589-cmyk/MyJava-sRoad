package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.dto.ReviewDTO;
import com.campus.secondhand.service.ReviewService;
import com.campus.secondhand.vo.ReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@Tag(name = "评价管理")
public class ReviewController {

    @Resource
    private ReviewService reviewService;

    @PostMapping("/add")
    @Operation(summary = "添加评价")
    public Result<String> add(@Valid @RequestBody ReviewDTO dto){
        reviewService.add(dto);
        return Result.success("评价成功");
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "查看用户评价")
    public Result<List<ReviewVO>> list(@PathVariable Long userid) {
        return Result.success(reviewService.list(userid));
    }




}
