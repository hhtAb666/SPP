/**
 * 用户管理控制器
 */
package com.campus.sport.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.sport.annotation.Log;
import com.campus.sport.common.Result;
import com.campus.sport.entity.User;
import com.campus.sport.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    
    @SaCheckLogin
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null); 
        }
        return Result.success("获取成功", user);
    }

    
    @SaCheckRole("ADMIN")
    @GetMapping("/list")
    public Result<Page<User>> getUserList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String username) {
        
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            queryWrapper.like(User::getUsername, username);
        }
        queryWrapper.orderByDesc(User::getCreateTime);
        
        return Result.success("查询成功", userService.page(pageParam, queryWrapper));
    }

    @Operation(summary = "添加用户(管理员)")
    @SaCheckRole("ADMIN")
    @PostMapping("/add")
    @Log("添加用户")
    public Result<String> add(@RequestBody User user) {
        userService.save(user);
        return Result.success("添加成功", null);
    }

    @Operation(summary = "更新用户(管理员)")
    @SaCheckRole("ADMIN")
    @PostMapping("/update")
    @Log("更新用户")
    public Result<String> update(@RequestBody User user) {
        userService.updateById(user);
        return Result.success("更新成功", null);
    }

    
    @SaCheckRole("ADMIN")
    @PostMapping("/delete/{id}")
    @Log("删除用户")
    public Result<String> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功", null);
    }
}