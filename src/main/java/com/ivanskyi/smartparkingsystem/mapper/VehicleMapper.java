package com.ivanskyi.smartparkingsystem.mapper;

import com.ivanskyi.smartparkingsystem.dto.VehicleDto;
import com.ivanskyi.smartparkingsystem.model.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    VehicleDto toDto(Vehicle vehicle);

    Vehicle toEntity(VehicleDto vehicleDto);
}
