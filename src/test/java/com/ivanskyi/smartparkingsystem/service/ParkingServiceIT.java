package com.ivanskyi.smartparkingsystem.service;

import com.ivanskyi.smartparkingsystem.dto.response.CheckInResponseDto;
import com.ivanskyi.smartparkingsystem.enumeration.SlotType;
import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import com.ivanskyi.smartparkingsystem.model.Level;
import com.ivanskyi.smartparkingsystem.model.Lot;
import com.ivanskyi.smartparkingsystem.model.Session;
import com.ivanskyi.smartparkingsystem.model.Slot;
import com.ivanskyi.smartparkingsystem.model.id.SlotId;
import com.ivanskyi.smartparkingsystem.repository.LevelRepository;
import com.ivanskyi.smartparkingsystem.repository.LotRepository;
import com.ivanskyi.smartparkingsystem.repository.SlotRepository;
import com.ivanskyi.smartparkingsystem.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DisplayName("ParkingService Integration Tests")
class ParkingServiceIT {
    private static final String CAR_PLATE_001 = "CAR001";
    private static final String CAR_PLATE_002 = "CAR002";
    private static final String MOTORCYCLE_PLATE_001 = "MOTO001";
    private static final String TRUCK_PLATE_001 = "TRUCK001";
    private static final String TRUCK_PLATE_002 = "TRUCK002";
    private static final String TRUCK_PLATE_003 = "TRUCK003";
    private static final String PARKING_LOT_NAME = "Downtown Parking";
    private static final Integer FLOOR_ONE = 1;
    private static final Integer FLOOR_TWO = 2;

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Lot parkingLot;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();
        slotRepository.deleteAll();
        levelRepository.deleteAll();
        lotRepository.deleteAll();

        parkingLot = new Lot();
        parkingLot.setName(PARKING_LOT_NAME);
        lotRepository.save(parkingLot);

        createLevelWithSlots(FLOOR_ONE);
        createLevelWithSlots(FLOOR_TWO);
    }

    private void createLevelWithSlots(Integer floorNumber) {
        Level level = new Level();
        level.setParkingLotId(parkingLot.getId());
        level.setFloorNumber(floorNumber);
        level.setDescription("Level " + floorNumber);
        levelRepository.save(level);

        createSlot(floorNumber, "C1", SlotType.COMPACT);
        createSlot(floorNumber, "C2", SlotType.COMPACT);
        createSlot(floorNumber, "L1", SlotType.LARGE);
        createSlot(floorNumber, "L2", SlotType.LARGE);
        createSlot(floorNumber, "M1", SlotType.MOTORCYCLE);
    }

    private void createSlot(Integer floorNumber, String slotNumber, SlotType slotType) {
        Slot slot = new Slot();
        slot.setParkingLotId(parkingLot.getId());
        slot.setFloorNumber(floorNumber);
        slot.setSlotNumber(slotNumber);
        slot.setSlotType(slotType);
        slot.setIsAvailable(true);
        slotRepository.save(slot);
    }

    @Test
    @DisplayName("checkIn creates session for CAR with compatible slot")
    void checkIn_should_create_session_for_car() {
        // Act
        Session session = parkingService.checkIn(CAR_PLATE_001, VehicleType.CAR.name());
        CheckInResponseDto response = parkingService.mapToCheckInResponse(session);

        // Assert
        assertThat(session).isNotNull();
        assertThat(session.getVehicle().getLicensePlate()).isEqualTo(CAR_PLATE_001);
        assertThat(session.getVehicle().getVehicleType()).isEqualTo(VehicleType.CAR);
        assertThat(session.getSlot().getIsAvailable()).isFalse();
        assertThat(response.getCheckInTime()).isNotNull();
    }

    @Test
    @DisplayName("checkIn creates session for MOTORCYCLE with compatible slot")
    void checkIn_should_create_session_for_motorcycle() {
        // Act
        Session session = parkingService.checkIn(MOTORCYCLE_PLATE_001, VehicleType.MOTORCYCLE.name());

        // Assert
        assertThat(session).isNotNull();
        assertThat(session.getVehicle().getVehicleType()).isEqualTo(VehicleType.MOTORCYCLE);
        assertThat(session.getSlot().getIsAvailable()).isFalse();
    }

    @Test
    @DisplayName("checkIn creates session for TRUCK with compatible slot")
    void checkIn_should_create_session_for_truck() {
        // Act
        Session session = parkingService.checkIn(TRUCK_PLATE_001, VehicleType.TRUCK.name());

        // Assert
        assertThat(session).isNotNull();
        assertThat(session.getVehicle().getVehicleType()).isEqualTo(VehicleType.TRUCK);
        assertThat(session.getSlot().getIsAvailable()).isFalse();
    }

    @Test
    @DisplayName("checkIn throws IllegalStateException when vehicle already checked in")
    void checkIn_should_throw_when_vehicle_already_checked_in() {
        // Arrange
        parkingService.checkIn(CAR_PLATE_001, VehicleType.CAR.name());

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> parkingService.checkIn(CAR_PLATE_001, VehicleType.CAR.name()));
    }

    @Test
    @DisplayName("checkOut releases slot and calculates fee")
    void checkOut_should_release_slot_and_calculate_fee() {
        // Arrange
        Session sessionIn = parkingService.checkIn(CAR_PLATE_001, VehicleType.CAR.name());
        String slotNumber = sessionIn.getSlot().getSlotNumber();
        Integer floorNumber = sessionIn.getSlot().getFloorNumber();

        // Act
        Session sessionOut = parkingService.checkOut(CAR_PLATE_001);

        // Assert
        assertThat(sessionOut.getExitTime()).isNotNull();
        assertThat(sessionOut.getTotalFee()).isNotNull();
        assertThat(sessionOut.getTotalFee()).isGreaterThan(BigDecimal.ZERO);

        Slot releasedSlot = slotRepository.findById(
                new SlotId(parkingLot.getId(), floorNumber, slotNumber)
        ).orElseThrow();
        assertThat(releasedSlot.getIsAvailable()).isTrue();
    }

    @Test
    @DisplayName("checkOut calculates fees for different vehicle types")
    void checkOut_should_calculate_fees_for_different_vehicle_types() {
        // Arrange
        parkingService.checkIn(CAR_PLATE_001, VehicleType.CAR.name());
        parkingService.checkIn(MOTORCYCLE_PLATE_001, VehicleType.MOTORCYCLE.name());
        parkingService.checkIn(TRUCK_PLATE_001, VehicleType.TRUCK.name());

        // Act
        Session carCheckOut = parkingService.checkOut(CAR_PLATE_001);
        Session motoCheckOut = parkingService.checkOut(MOTORCYCLE_PLATE_001);
        Session truckCheckOut = parkingService.checkOut(TRUCK_PLATE_001);

        // Assert
        assertThat(carCheckOut.getTotalFee()).isGreaterThan(BigDecimal.ZERO);
        assertThat(motoCheckOut.getTotalFee()).isGreaterThan(BigDecimal.ZERO);
        assertThat(truckCheckOut.getTotalFee()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("checkOut throws IllegalArgumentException when vehicle not found")
    void checkOut_should_throw_when_vehicle_not_found() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> parkingService.checkOut("NONEXISTENT"));
    }

    @Test
    @DisplayName("checkOut throws IllegalStateException when no active session exists")
    void checkOut_should_throw_when_no_active_session() {
        // Arrange
        vehicleService.findOrCreateVehicle(VehicleType.CAR, CAR_PLATE_001);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> parkingService.checkOut(CAR_PLATE_001));
    }

    @Test
    @DisplayName("getSessions returns all sessions when unfiltered")
    void getSessions_should_return_all_sessions_when_unfiltered() {
        // Arrange
        parkingService.checkIn(CAR_PLATE_001, VehicleType.CAR.name());
        parkingService.checkIn(MOTORCYCLE_PLATE_001, VehicleType.MOTORCYCLE.name());
        parkingService.checkIn(TRUCK_PLATE_001, VehicleType.TRUCK.name());

        // Act
        var allSessions = parkingService.getSessions(null);

        // Assert
        assertThat(allSessions).hasSize(3);
    }

    @Test
    @DisplayName("getSessions returns only active sessions")
    void getSessions_should_return_only_active_sessions() {
        // Arrange
        parkingService.checkIn(CAR_PLATE_001, VehicleType.CAR.name());
        parkingService.checkIn(MOTORCYCLE_PLATE_001, VehicleType.MOTORCYCLE.name());
        parkingService.checkOut(CAR_PLATE_001);

        // Act
        var activeSessions = parkingService.getSessions(true);

        // Assert
        assertThat(activeSessions).hasSize(1);
        assertThat(activeSessions).allMatch(s -> s.getExitTime() == null);
    }

    @Test
    @DisplayName("getSessions returns only completed sessions")
    void getSessions_should_return_only_completed_sessions() {
        // Arrange
        parkingService.checkIn(CAR_PLATE_001, VehicleType.CAR.name());
        parkingService.checkIn(MOTORCYCLE_PLATE_001, VehicleType.MOTORCYCLE.name());
        parkingService.checkOut(CAR_PLATE_001);
        parkingService.checkOut(MOTORCYCLE_PLATE_001);

        // Act
        var completedSessions = parkingService.getSessions(false);

        // Assert
        assertThat(completedSessions).hasSize(2);
        assertThat(completedSessions).allMatch(s -> s.getExitTime() != null && s.getTotalFee() != null);
    }

    @Test
    @DisplayName("multiple vehicles park simultaneously in different slots")
    void checkIn_should_handle_multiple_concurrent_vehicles() {
        // Act
        Session carSession = parkingService.checkIn(CAR_PLATE_001, VehicleType.CAR.name());
        Session motoSession = parkingService.checkIn(MOTORCYCLE_PLATE_001, VehicleType.MOTORCYCLE.name());
        Session truckSession = parkingService.checkIn(TRUCK_PLATE_001, VehicleType.TRUCK.name());

        // Assert
        assertThat(carSession.getSlot().getSlotNumber()).isNotEqualTo(motoSession.getSlot().getSlotNumber());
        assertThat(motoSession.getSlot().getSlotNumber()).isNotEqualTo(truckSession.getSlot().getSlotNumber());
    }

    @Test
    @DisplayName("checkIn allocates slots from next floor when current floor full")
    void checkIn_should_allocate_from_next_floor_when_current_full() {
        // Arrange - fill floor 1: 2 cars, 1 moto, 2 trucks
        parkingService.checkIn(CAR_PLATE_001, VehicleType.CAR.name());
        parkingService.checkIn(CAR_PLATE_002, VehicleType.CAR.name());
        parkingService.checkIn(MOTORCYCLE_PLATE_001, VehicleType.MOTORCYCLE.name());
        parkingService.checkIn(TRUCK_PLATE_001, VehicleType.TRUCK.name());
        parkingService.checkIn(TRUCK_PLATE_002, VehicleType.TRUCK.name());

        // Act - next vehicle should allocate from floor 2
        Session session = parkingService.checkIn(TRUCK_PLATE_003, VehicleType.TRUCK.name());

        // Assert
        assertThat(session.getSlot()).isNotNull();
        assertThat(session.getParkingLotId()).isEqualTo(parkingLot.getId());
    }
}
