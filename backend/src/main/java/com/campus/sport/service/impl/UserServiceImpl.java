/**
 * 用户业务实现类
 */
package com.campus.sport.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.sport.entity.User;
import com.campus.sport.mapper.UserMapper;
import com.campus.sport.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public String login(String username, String password) {
        
        if (!StringUtils.hasText(username)) {
            throw new RuntimeException("用户名不能为空");
        }
        if (!StringUtils.hasText(password)) {
            throw new RuntimeException("密码不能为空");
        }

        
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, username);
        User user = baseMapper.selectOne(query);

        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        
        if (user.getPassword() == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        
        StpUtil.login(user.getId());
        
        
        return StpUtil.getTokenValue();
    }

    @Override
    public void register(User user) {
        
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, user.getUsername());
        if (baseMapper.selectCount(query) > 0) {
            throw new RuntimeException("该学号已被注册");
        }

        
        user.setRole("USER");
        
        
        baseMapper.insert(user);
    }
}
