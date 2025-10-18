package com.ivanskyi.smartparkingsystem.service;

import com.ivanskyi.smartparkingsystem.dto.SessionDto;
import com.ivanskyi.smartparkingsystem.dto.SlotDto;
import com.ivanskyi.smartparkingsystem.dto.VehicleDto;
import com.ivanskyi.smartparkingsystem.dto.response.CheckInResponseDto;
import com.ivanskyi.smartparkingsystem.dto.response.CheckOutResponseDto;
import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import com.ivanskyi.smartparkingsystem.mapper.SessionMapper;
import com.ivanskyi.smartparkingsystem.mapper.SlotMapper;
import com.ivanskyi.smartparkingsystem.mapper.VehicleMapper;
import com.ivanskyi.smartparkingsystem.model.Session;
import com.ivanskyi.smartparkingsystem.model.Slot;
import com.ivanskyi.smartparkingsystem.model.Vehicle;
import com.ivanskyi.smartparkingsystem.repository.SlotRepository;
import com.ivanskyi.smartparkingsystem.service.pricing.PricingStrategyFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingService {
    private final VehicleService vehicleService;
    private final SessionService sessionService;
    private final PricingStrategyFactory pricingStrategyFactory;
    private final VehicleMapper vehicleMapper;
    private final SlotMapper slotMapper;
    private final SlotRepository slotRepository;
    private final SlotService slotService;
    private final SessionMapper sessionMapper;

    @Transactional
    public Session checkIn(String licensePlate, String vehicleTypeStr) {
        VehicleType type = VehicleType.valueOf(vehicleTypeStr.toUpperCase());
        Vehicle vehicle = vehicleService.findOrCreateVehicle(type, licensePlate);

        sessionService.findActiveSessionByVehicleId(vehicle.getId())
                .ifPresent(s -> { throw new IllegalStateException("Vehicle already checked in."); });

        Slot slot = slotService.findAvailableSlotFor(type)
                .orElseThrow(() -> new IllegalStateException("No available slot for type: " + type));

        slot.setIsAvailable(false);
        slotRepository.save(slot);

        Session session = new Session();
        session.setVehicle(vehicle);
        session.setSlot(slot);
        session.setParkingLotId(slot.getParkingLotId());
        session.setFloorNumber(slot.getFloorNumber());
        session.setSlotNumber(slot.getSlotNumber());
        session.setEntryTime(LocalDateTime.now());

        return sessionService.save(session);
    }

    @Transactional
    public Session checkOut(String licensePlate) {
        Vehicle vehicle = vehicleService.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + licensePlate));

        Session session = sessionService.findActiveSessionByVehicleId(vehicle.getId())
                .orElseThrow(() -> new IllegalStateException("No active session."));

        LocalDateTime exitTime = LocalDateTime.now();
        long hours = Math.max(1, Duration.between(session.getEntryTime(), exitTime).toHours());
        BigDecimal fee = pricingStrategyFactory.getStrategy(vehicle.getVehicleType()).calculateFee(hours);

        session.setExitTime(exitTime);
        session.setTotalFee(fee);
        sessionService.save(session);

        Slot slot = session.getSlot();
        slot.setIsAvailable(true);
        slotRepository.save(slot);

        return session;
    }

    public CheckInResponseDto mapToCheckInResponse(Session session) {
        VehicleDto vehicleDTO = vehicleMapper.toDto(session.getVehicle());
        SlotDto slotDTO = slotMapper.toDto(session.getSlot());
        return new CheckInResponseDto(vehicleDTO, slotDTO, session.getEntryTime());
    }

    public CheckOutResponseDto mapToCheckOutResponse(Session session) {
        VehicleDto vehicleDTO = vehicleMapper.toDto(session.getVehicle());
        SlotDto slotDTO = slotMapper.toDto(session.getSlot());
        long duration = Math.max(1, Duration.between(session.getEntryTime(), session.getExitTime()).toHours());
        return new CheckOutResponseDto(vehicleDTO, slotDTO, session.getEntryTime(), session.getExitTime(), duration, session.getTotalFee());
    }

    public List<SessionDto> getSessions(Boolean active) {
        List<Session> sessions;
        if (Boolean.TRUE.equals(active)) {
            sessions = sessionService.findAllActiveSessions();
        } else if (Boolean.FALSE.equals(active)) {
            sessions = sessionService.findAllInactiveSessions();
        } else {
            sessions = sessionService.findAllSessions();
        }
        return sessions.stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }
}
