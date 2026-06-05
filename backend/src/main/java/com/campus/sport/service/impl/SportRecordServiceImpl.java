/**
 * 运动记录业务实现类
 */
package com.campus.sport.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.sport.entity.SportRecord;
import com.campus.sport.mapper.SportRecordMapper;
import com.campus.sport.service.SportRecordService;
import org.springframework.stereotype.Service;

@Service
public class SportRecordServiceImpl extends ServiceImpl<SportRecordMapper, SportRecord> implements SportRecordService {
}
