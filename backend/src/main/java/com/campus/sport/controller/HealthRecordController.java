/**
 * 健康记录控制器
 */
package com.campus.sport.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.sport.common.Result;
import com.campus.sport.entity.HealthRecord;
import com.campus.sport.service.HealthRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Tag(name = "健康数据管理")
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    @Operation(summary = "添加健康记录")
    @SaCheckLogin
    @PostMapping("/add")
    public Result<String> add(@RequestBody HealthRecord healthRecord) {
        long userId = StpUtil.getLoginIdAsLong();
        healthRecord.setUserId(userId);
        if (healthRecord.getCreateTime() == null) {
            healthRecord.setCreateTime(LocalDateTime.now());
        }
        healthRecordService.save(healthRecord);
        return Result.success("添加成功", null);
    }

    @Operation(summary = "删除健康记录")
    @SaCheckLogin
    @PostMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        HealthRecord record = healthRecordService.getById(id);
        if (record == null) {
            return Result.error(404, "记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            return Result.error(403, "无权删除");
        }
        healthRecordService.removeById(id);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "获取我的健康记录")
    @SaCheckLogin
    @GetMapping("/my")
    public Result<Page<HealthRecord>> myRecords(@RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        long userId = StpUtil.getLoginIdAsLong();
        Page<HealthRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HealthRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HealthRecord::getUserId, userId)
                   .orderByDesc(HealthRecord::getRecordDate);
        return Result.success("获取成功", healthRecordService.page(pageParam, queryWrapper));
    }
}