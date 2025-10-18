package com.ivanskyi.smartparkingsystem.mapper;

import com.ivanskyi.smartparkingsystem.dto.SlotDto;
import com.ivanskyi.smartparkingsystem.model.Slot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SlotMapper {

    SlotDto toDto(Slot slot);

    @Mapping(target = "level", ignore = true)
    Slot toEntity(SlotDto slotDto);
}
