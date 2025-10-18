package com.ivanskyi.smartparkingsystem.service.pricing.impl;

import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import com.ivanskyi.smartparkingsystem.service.pricing.AbstractPricingStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class TruckPricingStrategy extends AbstractPricingStrategy {
    private static final BigDecimal TRUCK_HOURLY_RATE = new BigDecimal("3.00");

    public TruckPricingStrategy() {
        super(TRUCK_HOURLY_RATE, VehicleType.TRUCK);
    }
}