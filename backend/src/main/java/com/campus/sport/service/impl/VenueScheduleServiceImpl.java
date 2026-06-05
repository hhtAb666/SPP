/**
 * 场馆排班业务实现类
 */
package com.campus.sport.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.sport.entity.VenueSchedule;
import com.campus.sport.mapper.VenueScheduleMapper;
import com.campus.sport.service.VenueScheduleService;
import org.springframework.stereotype.Service;

@Service
public class VenueScheduleServiceImpl extends ServiceImpl<VenueScheduleMapper, VenueSchedule> implements VenueScheduleService {
}
