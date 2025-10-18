package com.ivanskyi.smartparkingsystem.service.pricing;

import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;

import java.math.BigDecimal;

public abstract class AbstractPricingStrategy implements PricingStrategy {

    private final BigDecimal hourlyRate;
    private final VehicleType vehicleType;

    protected AbstractPricingStrategy(BigDecimal hourlyRate, VehicleType vehicleType) {
        if (hourlyRate == null || hourlyRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Hourly rate must be non-negative");
        }

        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type can't be null");
        }

        this.hourlyRate = hourlyRate;
        this.vehicleType = vehicleType;
    }

    @Override
    public BigDecimal calculateFee(long hoursParked) {
        if (hoursParked < 0) {
            throw new IllegalArgumentException("Hours parked can't be negative");
        }

        return hourlyRate.multiply(BigDecimal.valueOf(hoursParked));
    }

    @Override
    public VehicleType getVehicleType() {
        return vehicleType;
    }
}
