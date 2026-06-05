/**
 * 预约数据访问接口
 */
package com.campus.sport.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.sport.entity.Reservation;
import com.campus.sport.entity.dto.ReservationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
    IPage<ReservationDTO> selectReservationPage(Page<ReservationDTO> page, @Param("userId") Long userId, @Param("status") Integer status);
}
