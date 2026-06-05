/**
 * 场馆管理控制器
 */
package com.campus.sport.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.sport.annotation.Log;
import com.campus.sport.common.Result;
import com.campus.sport.entity.Venue;
import com.campus.sport.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "场馆管理")
@RestController
@RequestMapping("/venue")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @Operation(summary = "获取场馆列表")
    @GetMapping("/list")
    public Result<Page<Venue>> list(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) String type) {
        Page<Venue> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Venue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null && !name.isEmpty(), Venue::getName, name)
                   .eq(type != null && !type.isEmpty(), Venue::getType, type);
        return Result.success("获取成功", venueService.page(pageParam, queryWrapper));
    }

    @Operation(summary = "添加场馆(管理员)")
    @SaCheckRole("ADMIN")
    @PostMapping("/add")
    @Log("添加场馆")
    public Result<String> add(@RequestBody Venue venue) {
        venueService.save(venue);
        return Result.success("添加成功", null);
    }

    @Operation(summary = "修改场馆(管理员)")
    @SaCheckRole("ADMIN")
    @PostMapping("/update")
    @Log("修改场馆")
    public Result<String> update(@RequestBody Venue venue) {
        venueService.updateById(venue);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "删除场馆(管理员)")
    @SaCheckRole("ADMIN")
    @PostMapping("/delete/{id}")
    @Log("删除场馆")
    public Result<String> delete(@PathVariable Long id) {
        venueService.removeById(id);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "获取场馆详情")
    @GetMapping("/{id}")
    public Result<Venue> getById(@PathVariable Long id) {
        return Result.success("获取成功", venueService.getById(id));
    }
}