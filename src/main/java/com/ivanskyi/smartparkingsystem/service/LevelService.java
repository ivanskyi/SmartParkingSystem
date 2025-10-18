package com.ivanskyi.smartparkingsystem.service;

import com.ivanskyi.smartparkingsystem.dto.LevelDto;
import com.ivanskyi.smartparkingsystem.mapper.LevelMapper;
import com.ivanskyi.smartparkingsystem.model.Level;
import com.ivanskyi.smartparkingsystem.model.Lot;
import com.ivanskyi.smartparkingsystem.repository.LevelRepository;
import com.ivanskyi.smartparkingsystem.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelService {
    private final LevelMapper levelMapper;
    private final LevelRepository levelRepository;
    private final LotRepository lotRepository;

    @Transactional(readOnly = true)
    public List<LevelDto> getAllByLotId(Long lotId) {
        return levelRepository.findByParkingLotId(lotId).stream()
                .map(levelMapper::toDto)
                .toList();
    }

    @Transactional
    public LevelDto add(Long lotId, LevelDto levelDTO) {
        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Parking lot not found with id: " + lotId));

        if (levelRepository.findByParkingLotIdAndFloorNumber(lotId, levelDTO.getFloorNumber()).isPresent()) {
            throw new IllegalArgumentException("Level already exists with parkingLotId: "
                    + lotId + " and floorNumber: "
                    + levelDTO.getFloorNumber());
        }

        Level level = new Level();
        level.setParkingLotId(lotId);
        level.setFloorNumber(levelDTO.getFloorNumber());
        level.setDescription(levelDTO.getDescription());
        level.setLot(lot);

        Level saved = levelRepository.save(level);
        return levelMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public LevelDto getById(Long lotId, Integer floorNumber) {
        Level level = levelRepository.findByParkingLotIdAndFloorNumber(lotId, floorNumber).orElseThrow(
                () -> new IllegalArgumentException("Level not found with parkingLotId: " + lotId
                        + " and floorNumber: " + floorNumber));

        return levelMapper.toDto(level);
    }

    @Transactional
    public void remove(Long parkingLotId, Integer floorNumber) {
        Level level = levelRepository.findByParkingLotIdAndFloorNumber(parkingLotId, floorNumber).orElseThrow(
                () -> new IllegalArgumentException("Level not found with parkingLotId: " + parkingLotId
                        + " and floorNumber: "
                        + floorNumber));

        levelRepository.delete(level);
    }
}
