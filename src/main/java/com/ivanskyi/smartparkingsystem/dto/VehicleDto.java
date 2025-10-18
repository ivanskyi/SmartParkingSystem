package com.ivanskyi.smartparkingsystem.dto;

import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import lombok.Data;

@Data
public class VehicleDto {
    private Long id;
    private String licensePlate;
    private VehicleType vehicleType;
}
