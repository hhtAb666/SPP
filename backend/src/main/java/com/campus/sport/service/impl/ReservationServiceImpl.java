/**
 * 预约业务实现类
 */
package com.campus.sport.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.sport.entity.Reservation;
import com.campus.sport.entity.VenueSchedule;
import com.campus.sport.entity.dto.ReservationDTO;
import com.campus.sport.mapper.ReservationMapper;
import com.campus.sport.mapper.VenueScheduleMapper;
import com.campus.sport.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    private final VenueScheduleMapper venueScheduleMapper;
    private final ReservationMapper reservationMapper;

    @Override
    public IPage<ReservationDTO> getReservationPage(Page<ReservationDTO> page, Long userId, Integer status) {
        return reservationMapper.selectReservationPage(page, userId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void makeReservation(Long userId, Long scheduleId) {
        
        
        
        
        
        VenueSchedule schedule = venueScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("排班不存在");
        }
        if (schedule.getStatus() == 0) {
            throw new RuntimeException("该场馆此时段暂停开放");
        }
        if (schedule.getCurrentPeople() >= schedule.getMaxPeople()) {
            throw new RuntimeException("该时段预约已满");
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setVenueId(schedule.getVenueId());
        reservation.setScheduleId(scheduleId);
        reservation.setStatus(0); // 0: 已预约
        reservation.setCreateTime(LocalDateTime.now());
        save(reservation);

        schedule.setCurrentPeople(schedule.getCurrentPeople() + 1);
        venueScheduleMapper.updateById(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReservation(Long userId, Long reservationId, Long newScheduleId) {
        
        Reservation reservation = getById(reservationId);
        if (reservation == null) throw new RuntimeException("预约不存在");
        if (!reservation.getUserId().equals(userId)) throw new RuntimeException("无权操作");
        if (reservation.getStatus() != 0) {
            throw new RuntimeException("当前状态无法修改");
        }
        
        Long oldScheduleId = reservation.getScheduleId();
        if (oldScheduleId.equals(newScheduleId)) return; 

        
        VenueSchedule newSchedule = venueScheduleMapper.selectById(newScheduleId);
        if (newSchedule == null || newSchedule.getStatus() == 0) throw new RuntimeException("新时段不可用");
        if (newSchedule.getCurrentPeople() >= newSchedule.getMaxPeople()) throw new RuntimeException("新时段已满");

        
        VenueSchedule oldSchedule = venueScheduleMapper.selectById(oldScheduleId);
        if (oldSchedule != null && oldSchedule.getCurrentPeople() > 0) {
            oldSchedule.setCurrentPeople(oldSchedule.getCurrentPeople() - 1);
            venueScheduleMapper.updateById(oldSchedule);
        }

        
        newSchedule.setCurrentPeople(newSchedule.getCurrentPeople() + 1);
        venueScheduleMapper.updateById(newSchedule);

        
        reservation.setScheduleId(newScheduleId);
        reservation.setVenueId(newSchedule.getVenueId()); 
        reservation.setCreateTime(LocalDateTime.now()); 
        reservation.setStatus(0); // 修改后状态重置为已预约
        updateById(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdateReservation(Long reservationId, Long newScheduleId) {
        Reservation reservation = getById(reservationId);
        if (reservation == null) throw new RuntimeException("预约不存在");
        
        if (reservation.getStatus() != 0) {
            throw new RuntimeException("当前状态无法修改");
        }
        
        Long oldScheduleId = reservation.getScheduleId();
        if (oldScheduleId.equals(newScheduleId)) return; 

        // 检查新时段
        VenueSchedule newSchedule = venueScheduleMapper.selectById(newScheduleId);
        if (newSchedule == null || newSchedule.getStatus() == 0) throw new RuntimeException("新时段不可用");
        if (newSchedule.getCurrentPeople() >= newSchedule.getMaxPeople()) throw new RuntimeException("新时段已满");

        // 释放旧时段
        VenueSchedule oldSchedule = venueScheduleMapper.selectById(oldScheduleId);
        if (oldSchedule != null && oldSchedule.getCurrentPeople() > 0) {
            oldSchedule.setCurrentPeople(oldSchedule.getCurrentPeople() - 1);
            venueScheduleMapper.updateById(oldSchedule);
        }

        // 占用新时段
        newSchedule.setCurrentPeople(newSchedule.getCurrentPeople() + 1);
        venueScheduleMapper.updateById(newSchedule);

        // 更新预约
        reservation.setScheduleId(newScheduleId);
        reservation.setVenueId(newSchedule.getVenueId()); 
        // 保持原创建时间或更新？通常修改不改变创建时间，但可能需要记录修改时间。这里先只改关联。
        reservation.setStatus(0); // 管理员修改后，状态强制变为已预约
        updateById(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = getById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此预约");
        }
        if (reservation.getStatus() != 0) { 
            throw new RuntimeException("当前状态无法取消");
        }

        reservation.setStatus(2);
        updateById(reservation);

        VenueSchedule schedule = venueScheduleMapper.selectById(reservation.getScheduleId());
        if (schedule != null && schedule.getCurrentPeople() > 0) {
            schedule.setCurrentPeople(schedule.getCurrentPeople() - 1);
            venueScheduleMapper.updateById(schedule);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditReservation(Long reservationId, Integer status) {
        Reservation reservation = getById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }
        
        
        if (reservation.getStatus() != 0) {
            throw new RuntimeException("当前状态无法操作");
        }

        reservation.setStatus(status);
        updateById(reservation);

        if (status == 2) {
            VenueSchedule schedule = venueScheduleMapper.selectById(reservation.getScheduleId());
            if (schedule != null && schedule.getCurrentPeople() > 0) {
                schedule.setCurrentPeople(schedule.getCurrentPeople() - 1);
                venueScheduleMapper.updateById(schedule);
            }
        }
    }
}
