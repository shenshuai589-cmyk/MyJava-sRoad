package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.pojo.Product;
import com.campus.secondhand.service.AdminService;
import com.campus.secondhand.vo.AdminUserVO;
import com.campus.secondhand.vo.DashboradVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员模块")
public class AdminController {

    @Resource
    private  AdminService adminService;


    /**
     * 查询待审核商品
     * @return
     */

    @GetMapping("/products")
    @Operation(summary = "查询待审核商品")
    public Result<List<Product>> auditList() {
        return Result.success(adminService.auditList());
    }

    /**
     * 审核商品
     */
    @PutMapping("/product/{id}/audit")
    @Operation(summary = "审核商品")
    public Result<Void> audit(@Parameter(description = "商品ID") Long id,
                              @Parameter(description = "审核状态：1通过 2拒绝")
                              @RequestParam Integer status) {
        adminService.audit(id, status);
        return Result.success();
    }

    @Operation(summary = "查询用户列表")
    @GetMapping("/users")
    public Result<List<AdminUserVO>> userList() {
        return Result.success(adminService.userList());
    }


    @Operation(summary = "修改用户状态")
    @PutMapping("/user/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable  Long id,
            @RequestParam Integer status) {

        adminService.updateUserStatus(id, status);

        return Result.success();
    }

    @Operation(summary = "后台数据统计")
    @GetMapping("/dashboard")
    public Result<DashboradVO> dashboard(){

        return Result.success(adminService.dashborad());
    }








}
