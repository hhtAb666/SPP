/**
 * 运动记录实体类
 */
package com.campus.sport.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sport_record")
public class SportRecord {
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
     * 运动类型
     */
    private String sportType;

    
    /**
     * 时长(分钟)
     */
    private Integer duration;

    
    /**
     * 消耗卡路里(kcal)
     */
    private Double calories;

    
    /**
     * 运动日期
     */
    private LocalDate recordDate;

    
    /**
     * 录入时间
     */
    private LocalDateTime createTime;
}
