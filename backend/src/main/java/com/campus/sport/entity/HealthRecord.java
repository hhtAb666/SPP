/**
 * 健康记录实体类
 */
package com.campus.sport.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("health_record")
public class HealthRecord {
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
     * 身高(cm)
     */
    private Double height;

    
    /**
     * 体重(kg)
     */
    private Double weight;

    
    /**
     * 记录日期
     */
    private LocalDate recordDate;

    
    /**
     * 录入时间
     */
    private LocalDateTime createTime;
}
