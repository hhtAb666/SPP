/**
 * 数据初始化配置类
 */
package com.campus.sport.config;

// 数据初始化配置类

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.sport.entity.User;
import com.campus.sport.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
// 数据初始化配置类
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> 正在检查初始数据...");
        checkAndInitAdmin();
    }

    private void checkAndInitAdmin() {
        
        long count = userService.count(new LambdaQueryWrapper<User>().eq(User::getUsername, "admin"));
        if (count == 0) {
            System.out.println(">>> 未检测到管理员账号，正在自动创建...");
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("123456"); 
            admin.setName("系统管理员");
            admin.setRole("ADMIN");
            admin.setCreateTime(LocalDateTime.now());
            
            
            userService.save(admin);
            System.out.println(">>> 管理员账号创建成功！账号: admin / 密码: 123456");
        } else {
            System.out.println(">>> 管理员账号已存在，跳过初始化。");
        }
    }
}
