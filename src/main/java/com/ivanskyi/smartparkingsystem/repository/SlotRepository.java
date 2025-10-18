package com.ivanskyi.smartparkingsystem.repository;

import com.ivanskyi.smartparkingsystem.enumeration.SlotType;
import com.ivanskyi.smartparkingsystem.model.Slot;
import com.ivanskyi.smartparkingsystem.model.id.SlotId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, SlotId> {

    List<Slot> findByParkingLotIdAndFloorNumber(Long parkingLotId, Integer floorNumber);

    Optional<Slot> findFirstBySlotTypeInAndIsAvailableTrue(List<SlotType> slotTypes);

    Optional<Slot> findByParkingLotIdAndFloorNumberAndSlotNumber(
            Long parkingLotId,
            Integer floorNumber,
            String slotNumber
    );
}
