/**
 * 场馆业务实现类
 */
package com.campus.sport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.sport.entity.Venue;
import com.campus.sport.entity.VenueSchedule;
import com.campus.sport.mapper.VenueMapper;
import com.campus.sport.service.VenueScheduleService;
import com.campus.sport.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl extends ServiceImpl<VenueMapper, Venue> implements VenueService {

    private final VenueScheduleService venueScheduleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Venue entity) {
        boolean success = super.save(entity);
        if (success) {
            generateSchedules(entity);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Venue entity) {
        boolean success = super.updateById(entity);
        if (success) {
            
            
            regenerateSchedules(entity);
        }
        return success;
    }

    private void regenerateSchedules(Venue venue) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        
        LambdaQueryWrapper<VenueSchedule> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(VenueSchedule::getVenueId, venue.getId())
                     .ge(VenueSchedule::getDate, tomorrow)
                     .eq(VenueSchedule::getCurrentPeople, 0); 
        venueScheduleService.remove(deleteWrapper);

        
        
        generateSchedules(venue);
    }

    
    private void generateSchedules(Venue venue) {
        
        String openDaysStr = StringUtils.hasText(venue.getOpenDays()) ? venue.getOpenDays() : "1,2,3,4,5,6,7";
        int startHour = venue.getOpenStartTime() != null ? venue.getOpenStartTime() : 9;
        int endHour = venue.getOpenEndTime() != null ? venue.getOpenEndTime() : 22;
        int maxPeople = venue.getDefaultMaxPeople() != null ? venue.getDefaultMaxPeople() : 20;

        List<Integer> openDays = Arrays.stream(openDaysStr.split(","))
                                       .map(Integer::parseInt)
                                       .collect(Collectors.toList());

        List<VenueSchedule> newSchedules = new ArrayList<>();
        LocalDate today = LocalDate.now();

        
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            int dayOfWeek = date.getDayOfWeek().getValue(); 

            
            if (!openDays.contains(dayOfWeek)) {
                continue;
            }

            
            for (int hour = startHour; hour < endHour; hour++) {
                
                boolean exists = venueScheduleService.count(new LambdaQueryWrapper<VenueSchedule>()
                        .eq(VenueSchedule::getVenueId, venue.getId())
                        .eq(VenueSchedule::getDate, date)
                        .eq(VenueSchedule::getTimeSlot, hour)) > 0;
                
                if (exists) {
                    continue;
                }

                VenueSchedule schedule = new VenueSchedule();
                schedule.setVenueId(venue.getId());
                schedule.setDate(date);
                schedule.setTimeSlot(hour);
                schedule.setMaxPeople(maxPeople);
                schedule.setCurrentPeople(0);
                schedule.setStatus(1); 
                newSchedules.add(schedule);
            }
        }
        
        if (!newSchedules.isEmpty()) {
            venueScheduleService.saveBatch(newSchedules);
        }
    }
}