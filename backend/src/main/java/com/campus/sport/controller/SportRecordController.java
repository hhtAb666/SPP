/**
 * 运动记录控制器
 */
package com.campus.sport.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.sport.common.Result;
import com.campus.sport.entity.SportRecord;
import com.campus.sport.service.SportRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Tag(name = "运动数据管理")
@RestController
@RequestMapping("/sport")
@RequiredArgsConstructor
public class SportRecordController {

    private final SportRecordService sportRecordService;

    @Operation(summary = "添加运动记录")
    @SaCheckLogin
    @PostMapping("/add")
    public Result<String> add(@RequestBody SportRecord sportRecord) {
        long userId = StpUtil.getLoginIdAsLong();
        sportRecord.setUserId(userId);
        if (sportRecord.getCreateTime() == null) {
            sportRecord.setCreateTime(LocalDateTime.now());
        }
        sportRecordService.save(sportRecord);
        return Result.success("添加成功", null);
    }

    @Operation(summary = "删除运动记录")
    @SaCheckLogin
    @PostMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        long userId = StpUtil.getLoginIdAsLong();
        SportRecord record = sportRecordService.getById(id);
        if (record == null) {
            return Result.error(404, "记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            return Result.error(403, "无权删除");
        }
        sportRecordService.removeById(id);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "获取我的运动记录")
    @SaCheckLogin
    @GetMapping("/my")
    public Result<Page<SportRecord>> myRecords(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size) {
        long userId = StpUtil.getLoginIdAsLong();
        Page<SportRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SportRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SportRecord::getUserId, userId)
                   .orderByDesc(SportRecord::getRecordDate);
        return Result.success("获取成功", sportRecordService.page(pageParam, queryWrapper));
    }
}