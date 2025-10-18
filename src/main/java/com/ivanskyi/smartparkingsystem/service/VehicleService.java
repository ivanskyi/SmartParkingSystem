package com.ivanskyi.smartparkingsystem.service;

import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import com.ivanskyi.smartparkingsystem.model.Vehicle;
import com.ivanskyi.smartparkingsystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Vehicle findOrCreateVehicle(VehicleType type, String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate).orElseGet(() -> {
            Vehicle vehicle = new Vehicle();
            vehicle.setLicensePlate(licensePlate);
            vehicle.setVehicleType(type);
            return vehicleRepository.save(vehicle);
        });
    }

    public Optional<Vehicle> findByLicensePlate(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate);
    }
}
