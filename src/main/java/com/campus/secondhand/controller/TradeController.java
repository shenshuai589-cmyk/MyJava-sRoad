package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.dto.TradeDTO;
import com.campus.secondhand.pojo.Trade;
import com.campus.secondhand.service.TradeService;
import com.campus.secondhand.vo.ProductDetailVO;
import com.campus.secondhand.vo.TradeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trade")
@Tag(name = "交易管理")
public class TradeController {

    @Resource
    private TradeService tradeService;

    @PostMapping("/create")
    @Operation(summary = "创建交易订单")
    public Result<Trade> create(@Valid @RequestBody TradeDTO dto){
        return Result.success(tradeService.create(dto));
    }

    @PutMapping("/confirm/{id}")
    @Operation(summary = "卖家确认订单")
    public Result<Void> confirm(@PathVariable Long id){
        tradeService.confirm(id);
        return Result.success();
    }

    @PutMapping("/finish/{id}")
    @Operation(summary = "完成交易")
    public Result<Void> finish(@PathVariable Long id){
        tradeService.finish(id);
        return Result.success();
    }

    @GetMapping("/buyer")
    @Operation(summary = "查询我买的订单")
    public Result<List<TradeVO>> buyerList(){
        return Result.success(
                tradeService.buyerList()
        );
    }

    @GetMapping("/seller")
    @Operation(summary = "查询我卖出的订单")
    public Result<List<TradeVO>> sellerList(){
        return Result.success(tradeService.sellerList());
    }

    // 取消订单
    @PutMapping("/cancel/{id}")
    @Operation(summary = "取消订单")
    public Result<Void> cancel(@PathVariable Long id) {
        tradeService.cancel(id);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "订单详情")
    public Result<TradeVO> detail(@PathVariable Long id){
        return Result.success(tradeService.detail(id));
    }
}
