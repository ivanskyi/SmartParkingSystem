package com.ivanskyi.smartparkingsystem.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckInRequestDto {
    private String licensePlate;
    private String vehicleType;
}
