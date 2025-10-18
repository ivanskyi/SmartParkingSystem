package com.ivanskyi.smartparkingsystem.service.pricing.impl;

import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import com.ivanskyi.smartparkingsystem.service.pricing.AbstractPricingStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class CarPricingStrategy extends AbstractPricingStrategy {
    private static final BigDecimal CAR_HOURLY_RATE = new BigDecimal("2.00");

    public CarPricingStrategy() {
        super(CAR_HOURLY_RATE, VehicleType.CAR);
    }
}