package com.ivanskyi.smartparkingsystem.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SessionDto {
    private Long id;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private BigDecimal totalFee;
    private VehicleDto vehicle;
    private SlotDto slot;
}
