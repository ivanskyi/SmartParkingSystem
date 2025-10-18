package com.ivanskyi.smartparkingsystem.service.pricing;

import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PricingStrategyFactory {

    private final Map<VehicleType, PricingStrategy> strategies;

    public PricingStrategyFactory(List<PricingStrategy> pricingStrategies) {
        this.strategies = pricingStrategies.stream()
                .collect(Collectors.toMap(PricingStrategy::getVehicleType, Function.identity()));
    }

    public PricingStrategy getStrategy(VehicleType vehicleType) {
        PricingStrategy strategy = strategies.get(vehicleType);
        if (strategy == null) {
            throw new IllegalArgumentException("No pricing strategy found for vehicle type: " + vehicleType);
        }

        return strategy;
    }
}
