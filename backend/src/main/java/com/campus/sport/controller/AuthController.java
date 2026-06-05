/**
 * 认证控制器 (登录/注册)
 */
package com.campus.sport.controller;

import com.campus.sport.common.Result;
import com.campus.sport.entity.User;
import com.campus.sport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginForm) {
        if (loginForm == null || loginForm.getUsername() == null || loginForm.getPassword() == null) {
            return Result.error(400, "用户名或密码不能为空");
        }
        
        
        String token = userService.login(loginForm.getUsername(), loginForm.getPassword());
        
        
        User user = userService.getOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginForm.getUsername()));
        
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", user); 
        
        return Result.success("登录成功", data);
    }

    
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        userService.register(user);
        return Result.success("注册成功", null);
    }
}