package com.ivanskyi.smartparkingsystem.controller.v1;

import com.ivanskyi.smartparkingsystem.dto.LotDto;
import com.ivanskyi.smartparkingsystem.service.LotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/lots")
public class LotController {

    private final LotService lotService;

    @GetMapping
    public ResponseEntity<List<LotDto>> getAll() {
        List<LotDto> lots = lotService.getAll();
        return ResponseEntity.ok(lots);
    }

    @PostMapping
    public ResponseEntity<LotDto> create(@RequestBody LotDto lot) {
        LotDto created = lotService.create(lot);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{lotId}")
    public ResponseEntity<LotDto> getById(@PathVariable("lotId") Long lotId) {
        LotDto lot = lotService.getById(lotId);
        return ResponseEntity.ok(lot);
    }

    @DeleteMapping("/{lotId}")
    public ResponseEntity<Void> delete(@PathVariable("lotId") Long lotId) {
        lotService.delete(lotId);
        return ResponseEntity.noContent().build();
    }
}
