/**
 * 系统日志业务实现类
 */
package com.campus.sport.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.sport.entity.SysLog;
import com.campus.sport.mapper.SysLogMapper;
import com.campus.sport.service.SysLogService;
import org.springframework.stereotype.Service;

@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {
}