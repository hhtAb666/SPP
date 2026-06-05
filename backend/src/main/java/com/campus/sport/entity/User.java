/**
 * 用户实体类
 */
package com.campus.sport.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户名/学号
     */
    private String username; 
    /**
     * 密码
     */
    private String password; 
    /**
     * 姓名
     */
    private String name;     
    /**
     * 角色: USER/ADMIN
     */
    private String role;     
    /**
     * 联系电话
     */
    private String phone;    
    private LocalDateTime createTime; 
}
