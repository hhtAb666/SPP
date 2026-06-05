/**
 * 预约数据传输对象
 */
package com.campus.sport.entity.dto;

import com.campus.sport.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReservationDTO extends Reservation {
    private String venueName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    private Integer timeSlot;
    
    private String userName; 
}