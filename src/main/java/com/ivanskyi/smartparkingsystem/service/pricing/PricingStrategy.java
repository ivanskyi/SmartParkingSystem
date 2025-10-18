package com.ivanskyi.smartparkingsystem.service.pricing;

import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculateFee(long hoursParked);

    VehicleType getVehicleType();
}
