package com.ivanskyi.smartparkingsystem.service.pricing.impl;

import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import com.ivanskyi.smartparkingsystem.service.pricing.AbstractPricingStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class MotorcyclePricingStrategy extends AbstractPricingStrategy {
    private static final BigDecimal MOTORCYCLE_HOURLY_RATE = new BigDecimal("1.00");

    public MotorcyclePricingStrategy() {
        super(MOTORCYCLE_HOURLY_RATE, VehicleType.MOTORCYCLE);
    }
}