/**
 * 预约管理控制器
 */
package com.campus.sport.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.sport.annotation.Log;
import com.campus.sport.common.Result;
import com.campus.sport.entity.dto.ReservationDTO;
import com.campus.sport.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "预约管理")
@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "提交预约")
    @SaCheckLogin
    @PostMapping("/add")
    public Result<String> add(@RequestParam Long scheduleId) {
        long userId = StpUtil.getLoginIdAsLong();
        reservationService.makeReservation(userId, scheduleId);
        return Result.success("预约成功", null);
    }

    @Operation(summary = "取消预约")
    @SaCheckLogin
    @PostMapping("/cancel/{id}")
    public Result<String> cancel(@PathVariable Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        reservationService.cancelReservation(userId, id);
        return Result.success("取消成功", null);
    }

    @Operation(summary = "获取我的预约列表")
    @SaCheckLogin
    @GetMapping("/my")
    public Result<IPage<ReservationDTO>> myReservations(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        long userId = StpUtil.getLoginIdAsLong();
        Page<ReservationDTO> pageParam = new Page<>(page, size);
        return Result.success("获取成功", reservationService.getReservationPage(pageParam, userId, null));
    }

    @Operation(summary = "获取所有预约(管理员)")
    @SaCheckRole("ADMIN")
    @GetMapping("/list")
    public Result<IPage<ReservationDTO>> list(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) Integer status) {
        Page<ReservationDTO> pageParam = new Page<>(page, size);
        return Result.success("获取成功", reservationService.getReservationPage(pageParam, null, status));
    }

    @Operation(summary = "修改预约")
    @SaCheckLogin
    @PostMapping("/update")
    @Log("修改预约")
    public Result<String> update(@RequestParam Long id, @RequestParam Long newScheduleId) {
        long userId = StpUtil.getLoginIdAsLong();
        reservationService.updateReservation(userId, id, newScheduleId);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "管理员修改预约")
    @SaCheckRole("ADMIN")
    @PostMapping("/admin/update")
    @Log("管理员修改预约")
    public Result<String> adminUpdate(@RequestParam Long id, @RequestParam Long newScheduleId) {
        reservationService.adminUpdateReservation(id, newScheduleId);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "审核预约(管理员)")
    @SaCheckRole("ADMIN")
    @PostMapping("/audit/{id}")
    @Log("审核预约")
    public Result<String> audit(@PathVariable Long id, @RequestParam Integer status) {
        reservationService.auditReservation(id, status);
        return Result.success("操作成功", null);
    }
}