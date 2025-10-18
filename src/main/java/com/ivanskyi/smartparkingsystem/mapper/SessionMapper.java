package com.ivanskyi.smartparkingsystem.mapper;

import com.ivanskyi.smartparkingsystem.dto.SessionDto;
import com.ivanskyi.smartparkingsystem.model.Session;
import org.mapstruct.Mapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {VehicleMapper.class, SlotMapper.class})
public interface SessionMapper {

    SessionDto toDto(Session session);

    @AfterMapping
    default void linkSlot(@MappingTarget Session session, SessionDto sessionDto) {
        if (sessionDto.getSlot() != null) {
            session.setParkingLotId(sessionDto.getSlot().getParkingLotId());
            session.setFloorNumber(sessionDto.getSlot().getFloorNumber());
            session.setSlotNumber(sessionDto.getSlot().getSlotNumber());
        }
    }
}