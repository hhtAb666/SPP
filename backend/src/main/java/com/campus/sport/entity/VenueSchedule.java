/**
 * 场馆排班实体类
 */
package com.campus.sport.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("venue_schedule")
public class VenueSchedule {
    @TableId(type = IdType.AUTO)
    /**
     * 主键ID
     */
    private Long id;

    
    /**
     * 场馆ID
     */
    private Long venueId;

    
    /**
     * 日期
     */
    private LocalDate date;

    
    /**
     * 时段(9代表9:00-10:00)
     */
    private Integer timeSlot;

    
    /**
     * 最大人数
     */
    private Integer maxPeople;

    
    /**
     * 当前已约人数
     */
    private Integer currentPeople;

    
    /**
     * 状态: 1开放, 0关闭
     */
    private Integer status;
}
