package com.ivanskyi.smartparkingsystem.mapper;

import com.ivanskyi.smartparkingsystem.dto.LevelDto;
import com.ivanskyi.smartparkingsystem.model.Level;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SlotMapper.class)
public interface LevelMapper {

    LevelDto toDto(Level level);
}
