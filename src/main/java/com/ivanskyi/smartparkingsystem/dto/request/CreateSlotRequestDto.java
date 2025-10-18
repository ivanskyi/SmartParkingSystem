package com.ivanskyi.smartparkingsystem.dto.request;

import com.ivanskyi.smartparkingsystem.enumeration.SlotType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSlotRequestDto {
    private Long slotNumber;
    private SlotType type;
}
