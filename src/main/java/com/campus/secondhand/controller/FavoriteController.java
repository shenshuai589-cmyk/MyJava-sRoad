package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.pojo.Favorite;
import com.campus.secondhand.service.FavoriteService;
import com.campus.secondhand.vo.FavoriteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
@Tag(name = "商品收藏")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;


    /**
     * 收藏商品
     * @param productId
     * @return
     */

    @PostMapping("/add/{productId}")
    @Operation(summary = "收藏商品")
    public Result<String> add(@PathVariable Long productId) {
        favoriteService.add(productId);
        return Result.success("收藏成功");
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "取消收藏")
    public Result<String> delete(@PathVariable Long productId) {
        favoriteService.delete(productId);
        return Result.success("取消收藏成功");
    }
    /**
     * 我的收藏
     */
    @GetMapping("/my")
    @Operation(summary = "我的收藏列表")
    public Result<List<FavoriteVO>> my(){
        return Result.success(favoriteService.myFavorites());
    }
}
