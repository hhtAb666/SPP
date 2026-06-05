/**
 * 用户业务接口
 */
package com.campus.sport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.sport.entity.User;

public interface UserService extends IService<User> {
    
    
    String login(String username, String password);

    
    void register(User user);
}
