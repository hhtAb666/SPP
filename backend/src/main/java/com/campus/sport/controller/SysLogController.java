/**
 * 系统日志控制器
 */
package com.campus.sport.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.sport.common.Result;
import com.campus.sport.entity.SysLog;
import com.campus.sport.service.SysLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
@Tag(name = "Log Management")
@RequiredArgsConstructor
public class SysLogController {

    private final SysLogService sysLogService;

    @GetMapping("/list")
    @Operation(summary = "Get Log List")
    @SaCheckRole("ADMIN")
    public Result<Page<SysLog>> list(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) String username,
                                     @RequestParam(required = false) String operation) {
        Page<SysLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysLog::getUsername, username)
               .like(StringUtils.hasText(operation), SysLog::getOperation, operation)
               .orderByDesc(SysLog::getCreateTime);
        return Result.success(sysLogService.page(pageParam, wrapper));
    }
}