package com.ivanskyi.smartparkingsystem.dto.response;

import com.ivanskyi.smartparkingsystem.dto.SlotDto;
import com.ivanskyi.smartparkingsystem.dto.VehicleDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CheckInResponseDto {
    private VehicleDto vehicle;
    private SlotDto slot;
    private LocalDateTime checkInTime;
}
