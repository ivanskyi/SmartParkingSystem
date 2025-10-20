package com.ivanskyi.smartparkingsystem.mapper;

import com.ivanskyi.smartparkingsystem.dto.LotDto;
import com.ivanskyi.smartparkingsystem.model.Lot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = LevelMapper.class)
public interface LotMapper {

    LotDto toDto(Lot lot);
}
