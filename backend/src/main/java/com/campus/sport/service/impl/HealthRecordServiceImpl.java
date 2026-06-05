/**
 * 健康记录业务实现类
 */
package com.campus.sport.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.sport.entity.HealthRecord;
import com.campus.sport.mapper.HealthRecordMapper;
import com.campus.sport.service.HealthRecordService;
import org.springframework.stereotype.Service;

@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {
}
