/**
 * 预约业务接口
 */
package com.campus.sport.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.sport.entity.Reservation;
import com.campus.sport.entity.dto.ReservationDTO;

public interface ReservationService extends IService<Reservation> {
    
    IPage<ReservationDTO> getReservationPage(Page<ReservationDTO> page, Long userId, Integer status);

    
    void makeReservation(Long userId, Long scheduleId);

    
    void cancelReservation(Long userId, Long reservationId);
    
    
    void updateReservation(Long userId, Long reservationId, Long newScheduleId);

    /**
     * 管理员修改预约
     * @param reservationId 预约ID
     * @param newScheduleId 新排班ID
     */
    void adminUpdateReservation(Long reservationId, Long newScheduleId);

    
    void auditReservation(Long reservationId, Integer status);
}
