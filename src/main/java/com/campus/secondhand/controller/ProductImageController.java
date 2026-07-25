package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/product/image")
@Tag(name = "商品图片")
public class ProductImageController {

    @Resource
    private ProductImageService productImageService;

    @PostMapping("/upload")
    @Operation(summary = "上传商品图片")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        String url = productImageService.upload(file);

        return Result.success(url);
    }





}
