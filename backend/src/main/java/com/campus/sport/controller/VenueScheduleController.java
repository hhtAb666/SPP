/**
 * 场馆排班控制器
 */
package com.campus.sport.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.sport.annotation.Log;
import com.campus.sport.common.Result;
import com.campus.sport.entity.VenueSchedule;
import com.campus.sport.service.VenueScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "场馆排班管理")
@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class VenueScheduleController {

    private final VenueScheduleService venueScheduleService;

    @Operation(summary = "获取排班列表")
    @GetMapping("/list")
    public Result<List<VenueSchedule>> list(@RequestParam(required = false) Long venueId, 
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        LambdaQueryWrapper<VenueSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(venueId != null, VenueSchedule::getVenueId, venueId)
                   .eq(date != null, VenueSchedule::getDate, date)
                   .orderByAsc(VenueSchedule::getDate)
                   .orderByAsc(VenueSchedule::getTimeSlot);
        return Result.success("获取成功", venueScheduleService.list(queryWrapper));
    }

    @Operation(summary = "发布/修改排班(管理员)")
    @SaCheckRole("ADMIN")
    @PostMapping("/save")
    @Log("发布/修改排班")
    public Result<String> save(@RequestBody VenueSchedule schedule) {
        
        if (schedule.getId() == null && schedule.getCurrentPeople() == null) {
            schedule.setCurrentPeople(0);
        }
        venueScheduleService.saveOrUpdate(schedule);
        return Result.success("保存成功", null);
    }

    @Operation(summary = "批量生成排班(管理员)")
    @SaCheckRole("ADMIN")
    @PostMapping("/batchSave")
    @Log("批量生成排班")
    public Result<String> batchSave(@RequestBody List<VenueSchedule> scheduleList) {
        venueScheduleService.saveBatch(scheduleList);
        return Result.success("批量保存成功", null);
    }
    
    @Operation(summary = "删除排班(管理员)")
    @SaCheckRole("ADMIN")
    @PostMapping("/delete/{id}")
    @Log("删除排班")
    public Result<String> delete(@PathVariable Long id) {
        venueScheduleService.removeById(id);
        return Result.success("删除成功", null);
    }
}