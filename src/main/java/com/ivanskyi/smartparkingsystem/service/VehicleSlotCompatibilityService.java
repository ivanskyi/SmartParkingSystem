package com.ivanskyi.smartparkingsystem.service;

import com.ivanskyi.smartparkingsystem.enumeration.SlotType;
import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VehicleSlotCompatibilityService {
    private final Map<VehicleType, List<SlotType>> compatibility;

    public List<SlotType> getCompatibleSlots(VehicleType vehicleType) {
        return compatibility.getOrDefault(vehicleType, List.of());
    }
}
