/**
 * Sa-Token 权限加载接口实现类
 */
package com.campus.sport.config;

// Sa-Token权限配置类

import cn.dev33.satoken.stp.StpInterface;
import com.campus.sport.entity.User;
import com.campus.sport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
@RequiredArgsConstructor
// Sa-Token权限配置类
public class StpInterfaceImpl implements StpInterface {
    private final UserService userService;

    
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        
        return new ArrayList<>();
    }

    
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        
        Long userId;
        try {
            userId = Long.valueOf(loginId.toString());
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }

        
        User user = userService.getById(userId);
        if (user == null) {
            return new ArrayList<>();
        }

        
        
        
        if (user.getRole() != null) {
            return Collections.singletonList(user.getRole());
        }
        return new ArrayList<>();
    }
}