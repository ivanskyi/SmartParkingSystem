package com.ivanskyi.smartparkingsystem.service;

import com.ivanskyi.smartparkingsystem.dto.SlotDto;
import com.ivanskyi.smartparkingsystem.dto.request.CreateSlotRequestDto;
import com.ivanskyi.smartparkingsystem.enumeration.SlotType;
import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import com.ivanskyi.smartparkingsystem.mapper.SlotMapper;
import com.ivanskyi.smartparkingsystem.model.Level;
import com.ivanskyi.smartparkingsystem.model.Slot;
import com.ivanskyi.smartparkingsystem.repository.LevelRepository;
import com.ivanskyi.smartparkingsystem.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlotService {
    private final SlotRepository slotRepository;
    private final LevelRepository levelRepository;
    private final SlotMapper slotMapper;
    private final VehicleSlotCompatibilityService vehicleSlotCompatibilityService;

    @Transactional(readOnly = true)
    public List<SlotDto> getSlots(Long parkingLotId, Integer floorNumber) {
        return slotRepository.findByParkingLotIdAndFloorNumber(parkingLotId, floorNumber).stream()
                .map(slotMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SlotDto getSlot(Long parkingLotId, Integer floorNumber, String slotNumber) {
        Slot slot = slotRepository.findByParkingLotIdAndFloorNumberAndSlotNumber(parkingLotId, floorNumber, slotNumber)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        return slotMapper.toDto(slot);
    }

    @Transactional
    public SlotDto addSlotToLevel(Long parkingLotId, Integer floorNumber, CreateSlotRequestDto request) {
        String slotNumber = String.valueOf(request.getSlotNumber());

        if (slotRepository
                .findByParkingLotIdAndFloorNumberAndSlotNumber(parkingLotId, floorNumber, slotNumber)
                .isPresent()) {
            throw new IllegalArgumentException("Slot already exists");
        }

        Level level = levelRepository
                .findByParkingLotIdAndFloorNumber(parkingLotId, floorNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Level not found for parkingLotId: " + parkingLotId + " and floorNumber: " + floorNumber));

        Slot slot = new Slot();
        slot.setParkingLotId(parkingLotId);
        slot.setFloorNumber(floorNumber);
        slot.setSlotNumber(slotNumber);
        slot.setSlotType(request.getType());
        slot.setIsAvailable(true);
        slot.setLevel(level);

        Slot savedSlot = slotRepository.save(slot);
        return slotMapper.toDto(savedSlot);
    }

    @Transactional
    public void removeSlotFromLevel(Long parkingLotId, Integer floorNumber, String slotNumber) {
        Slot slot = slotRepository.findByParkingLotIdAndFloorNumberAndSlotNumber(parkingLotId, floorNumber, slotNumber)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        slotRepository.delete(slot);
    }

    @Transactional
    public SlotDto setSlotAvailability(Long parkingLotId, Integer floorNumber, String slotNumber, boolean available) {
        Slot slot = slotRepository.findByParkingLotIdAndFloorNumberAndSlotNumber(parkingLotId, floorNumber, slotNumber)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        slot.setIsAvailable(available);
        Slot updatedSlot = slotRepository.save(slot);
        return slotMapper.toDto(updatedSlot);
    }

    public Optional<Slot> findAvailableSlotFor(VehicleType vehicleType) {
        List<SlotType> types = vehicleSlotCompatibilityService.getCompatibleSlots(vehicleType);
        return slotRepository.findFirstBySlotTypeInAndIsAvailableTrue(types);
    }
}
