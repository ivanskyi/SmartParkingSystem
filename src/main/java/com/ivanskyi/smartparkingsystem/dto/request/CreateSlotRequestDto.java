package com.ivanskyi.smartparkingsystem.dto.request;

import com.ivanskyi.smartparkingsystem.enumeration.SlotType;
import lombok.Data;

@Data
public class CreateSlotRequestDto {
    private Long slotNumber;
    private SlotType type;
}
