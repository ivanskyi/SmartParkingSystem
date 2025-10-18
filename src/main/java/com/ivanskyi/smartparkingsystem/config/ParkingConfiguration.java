package com.ivanskyi.smartparkingsystem.config;

import com.ivanskyi.smartparkingsystem.enumeration.SlotType;
import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ParkingConfiguration {

    @Bean
    public Map<VehicleType, List<SlotType>> vehicleSlotCompatibility() {
        Map<VehicleType, List<SlotType>> rules = new HashMap<>();

        rules.put(VehicleType.CAR, List.of(SlotType.COMPACT, SlotType.LARGE));
        rules.put(VehicleType.MOTORCYCLE, List.of(SlotType.MOTORCYCLE, SlotType.COMPACT));
        rules.put(VehicleType.TRUCK, List.of(SlotType.LARGE));

        return Map.copyOf(rules);
    }
}

