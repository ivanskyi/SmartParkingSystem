package com.ivanskyi.smartparkingsystem.mapper;

import com.ivanskyi.smartparkingsystem.dto.SessionDto;
import com.ivanskyi.smartparkingsystem.model.Session;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {VehicleMapper.class, SlotMapper.class})
public interface SessionMapper {

    SessionDto toDto(Session session);
}
