/**
 * 运动记录数据访问接口
 */
package com.campus.sport.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.sport.entity.SportRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SportRecordMapper extends BaseMapper<SportRecord> {
}
