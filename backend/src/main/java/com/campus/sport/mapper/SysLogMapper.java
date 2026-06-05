/**
 * 系统日志数据访问接口
 */
package com.campus.sport.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.sport.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {
}