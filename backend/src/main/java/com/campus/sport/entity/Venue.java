/**
 * 场馆实体类
 */
package com.campus.sport.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("venue")
public class Venue {
    @TableId(type = IdType.AUTO)
    /**
     * 主键ID
     */
    private Long id;

    
    /**
     * 场馆名称
     */
    private String name;

    
    /**
     * 场馆类型
     */
    private String type;

    
    /**
     * 位置
     */
    private String location;

    
    /**
     * 状态: 1正常, 0维护
     */
    private Integer status;

    
    /**
     * 开放日期(1-7, 逗号分隔)
     */
    private String openDays;

    
    /**
     * 开放开始时间(0-23)
     */
    private Integer openStartTime;

    
    /**
     * 开放结束时间(0-23)
     */
    private Integer openEndTime;

    
    /**
     * 默认最大人数
     */
    private Integer defaultMaxPeople;
}