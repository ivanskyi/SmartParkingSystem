package com.ivanskyi.smartparkingsystem.dto;

import com.ivanskyi.smartparkingsystem.enumeration.SlotType;
import lombok.Data;

@Data
public class SlotDto {
    private Long parkingLotId;
    private Integer floorNumber;
    private String slotNumber;
    private SlotType slotType;
    private Boolean isAvailable;
}
