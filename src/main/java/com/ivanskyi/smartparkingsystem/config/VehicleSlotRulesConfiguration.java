package com.ivanskyi.smartparkingsystem.config;

import com.ivanskyi.smartparkingsystem.enumeration.SlotType;
import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class VehicleSlotRulesConfiguration {

    @Bean
    public Map<VehicleType, List<SlotType>> vehicleSlotRules() {
        return Map.of(
                VehicleType.CAR, List.of(SlotType.COMPACT,SlotType.LARGE),
                VehicleType.MOTORCYCLE, List.of(SlotType.MOTORCYCLE, SlotType.COMPACT),
                VehicleType.TRUCK, List.of(SlotType.LARGE)
        );
    }
}
