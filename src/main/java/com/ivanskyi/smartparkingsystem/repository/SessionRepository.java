package com.ivanskyi.smartparkingsystem.repository;

import com.ivanskyi.smartparkingsystem.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByVehicleIdAndExitTimeIsNull(Long vehicleId);

    List<Session> findByExitTimeIsNull();

    List<Session> findByExitTimeIsNotNull();

    List<Session> findByParkingLotIdAndFloorNumberAndSlotNumberAndExitTimeIsNull(
            Long parkingLotId,
            Integer floorNumber,
            String slotNumber
    );
}
