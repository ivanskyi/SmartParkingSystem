package com.ivanskyi.smartparkingsystem.service;

import com.ivanskyi.smartparkingsystem.dto.LotDto;
import com.ivanskyi.smartparkingsystem.mapper.LotMapper;
import com.ivanskyi.smartparkingsystem.model.Lot;
import com.ivanskyi.smartparkingsystem.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LotService {
    private final LotRepository lotRepository;
    private final LotMapper lotMapper;

    public List<LotDto> getAll() {
        return lotRepository.findAll().stream()
                .map(lotMapper::toDto)
                .toList();
    }

    public LotDto create(LotDto lotDto) {
        Lot lot = new Lot();
        lot.setName(lotDto.getName());
        Lot saved = lotRepository.save(lot);
        return lotMapper.toDto(saved);
    }

    public LotDto getById(Long lotId) {
        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Lot not found with id: " + lotId));

        return lotMapper.toDto(lot);
    }

    public void delete(Long lotId) {
        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Lot not found with id: " + lotId));

        lotRepository.delete(lot);
    }
}
