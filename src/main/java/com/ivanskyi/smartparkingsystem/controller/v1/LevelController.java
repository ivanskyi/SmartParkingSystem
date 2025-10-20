package com.ivanskyi.smartparkingsystem.controller.v1;

import com.ivanskyi.smartparkingsystem.dto.LevelDto;
import com.ivanskyi.smartparkingsystem.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/lots/{lotId}/levels")
public class LevelController {
    private final LevelService levelService;

    @GetMapping
    public ResponseEntity<List<LevelDto>> getAll(@PathVariable("lotId") Long lotId) {
        List<LevelDto> levels = levelService.getAllByLotId(lotId);
        return ResponseEntity.ok(levels);
    }

    @PostMapping
    public ResponseEntity<LevelDto> add(@PathVariable("lotId") Long lotId,
                                        @RequestBody LevelDto levelDto) {
        LevelDto added = levelService.add(lotId, levelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(added);
    }

    @GetMapping("/{floorNumber}")
    public ResponseEntity<LevelDto> getById(@PathVariable("lotId") Long lotId,
                                            @PathVariable("floorNumber") Integer floorNumber) {
        LevelDto level = levelService.getById(lotId, floorNumber);
        return ResponseEntity.ok(level);
    }

    @DeleteMapping("/{floorNumber}")
    public ResponseEntity<Void> remove(@PathVariable("lotId") Long lotId,
                                       @PathVariable("floorNumber") Integer floorNumber) {
        levelService.remove(lotId, floorNumber);
        return ResponseEntity.noContent().build();
    }
}
