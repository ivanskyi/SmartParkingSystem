package com.ivanskyi.smartparkingsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class LevelDto {
    private Long parkingLotId;
    private Integer floorNumber;
    private String description;
    private List<SlotDto> slots;
}
