package com.ivanskyi.smartparkingsystem.dto.response;

import com.ivanskyi.smartparkingsystem.dto.SlotDto;
import com.ivanskyi.smartparkingsystem.dto.VehicleDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CheckOutResponseDto {
    private VehicleDto vehicle;
    private SlotDto slot;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private long durationHours;
    private BigDecimal fee;
}
