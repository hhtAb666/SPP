/**
 * 场馆排班数据访问接口
 */
package com.campus.sport.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.sport.entity.VenueSchedule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VenueScheduleMapper extends BaseMapper<VenueSchedule> {
}
