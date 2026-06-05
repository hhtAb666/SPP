/**
 * 预约实体类
 */
package com.campus.sport.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reservation")
public class Reservation {
    @TableId(type = IdType.AUTO)
    /**
     * 主键ID
     */
    private Long id;

    
    /**
     * 用户ID
     */
    private Long userId;

    
    /**
     * 场馆ID
     */
    private Long venueId;

    
    /**
     * 排班ID
     */
    private Long scheduleId;

    
    /**
     * 状态: 0已预约, 1已完成, 2已取消, 3已拒绝
     */
    private Integer status;

    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
