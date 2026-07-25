package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.dto.ProductDTO;
import com.campus.secondhand.dto.ProductQueryDTO;
import com.campus.secondhand.pojo.Product;
import com.campus.secondhand.service.ProductService;
import com.campus.secondhand.service.TradeService;
import com.campus.secondhand.vo.ProductDetailVO;
import com.campus.secondhand.vo.ProductVO;
import com.campus.secondhand.vo.TradeVO;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@Tag(name = "商品管理")
public class ProductController {

    @Resource
    private ProductService productService;
    @Autowired
    private TradeService tradeService;

    @PostMapping("/add")
    @Operation(summary = "发布商品")
    public Result<ProductVO> add(@Valid @RequestBody ProductDTO productDTO){
        Product product = productService.add(productDTO);
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product,vo);

        return Result.success(vo);
    }

    @GetMapping("/list")
    @Operation(summary = "商品分页查询")
    public Result<PageInfo<ProductVO>> list(ProductQueryDTO queryDTO){
        PageInfo<Product> pageInfo = productService.page(queryDTO);

        PageInfo<ProductVO> voPageInfo = new PageInfo<>();

        BeanUtils.copyProperties(pageInfo,voPageInfo);

        List<ProductVO> list = pageInfo.getList()
                .stream()
                .map(product -> {
                    ProductVO vo = new ProductVO();
                    BeanUtils.copyProperties(product,vo);
                    return vo;
                })
                .toList();
        voPageInfo.setList(list);

        return Result.success(voPageInfo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "商品详情")
    public Result<ProductDetailVO> detail(@PathVariable Long id){
        return Result.success(productService.detail(id));
    }


    @GetMapping("/my")
    @Operation(summary = "我的商品")
    public Result<PageInfo<Product>> my(ProductQueryDTO dto){

        return Result.success(productService.myProducts(dto));
    }

    //下架
    @PutMapping("/{id}/offline")
    @Operation(summary = "商品下架")
    public Result<Void> offline(@PathVariable Long id){
        productService.offline(id);

        return Result.success();
    }

    //上架
    @PutMapping("/{id}/online")
    @Operation(summary = "商品上架")
    public Result<Void> online(@PathVariable Long id){
        productService.online(id);
        return Result.success();
    }

    //删除
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品")
    public Result<Void> delete(@PathVariable Long id){
        productService.delete(id);
        return Result.success();
    }

    //修改

    @PutMapping("/update/{id}")
    @Operation(summary = "修改商品")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto){
        productService.update(id, dto);
        return Result.success();
    }
}
