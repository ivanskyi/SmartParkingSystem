package com.ivanskyi.smartparkingsystem.repository;

import com.ivanskyi.smartparkingsystem.model.Level;
import com.ivanskyi.smartparkingsystem.model.id.LevelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LevelRepository extends JpaRepository<Level, LevelId> {

    List<Level> findByParkingLotId(Long parkingLotId);

    Optional<Level> findByParkingLotIdAndFloorNumber(Long parkingLotId, Integer floorNumber);
}
