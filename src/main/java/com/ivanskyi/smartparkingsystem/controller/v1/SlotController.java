package com.ivanskyi.smartparkingsystem.controller.v1;

import com.ivanskyi.smartparkingsystem.dto.SlotDto;
import com.ivanskyi.smartparkingsystem.dto.request.CreateSlotRequestDto;
import com.ivanskyi.smartparkingsystem.dto.request.UpdateSlotAvailabilityRequestDto;
import com.ivanskyi.smartparkingsystem.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/lots/{lotId}/levels/{floorNumber}/slots")
@RequiredArgsConstructor
public class SlotController {
    private final SlotService slotService;

    @GetMapping
    public ResponseEntity<List<SlotDto>> listSlots(@PathVariable Long lotId,
                                                   @PathVariable Integer floorNumber) {
        return ResponseEntity.ok(slotService.getSlots(lotId, floorNumber));
    }

    @PostMapping
    public ResponseEntity<SlotDto> createSlot(@PathVariable Long lotId,
                                              @PathVariable Integer floorNumber,
                                              @RequestBody CreateSlotRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(slotService.addSlotToLevel(lotId, floorNumber, request));
    }

    @GetMapping("/{slotNumber}")
    public ResponseEntity<SlotDto> getSlot(@PathVariable Long lotId,
                                           @PathVariable Integer floorNumber,
                                           @PathVariable String slotNumber) {
        return ResponseEntity.ok(slotService.getSlot(lotId, floorNumber, slotNumber));
    }

    @PatchMapping("/{slotNumber}")
    public ResponseEntity<SlotDto> updateAvailability(@PathVariable Long lotId,
                                                      @PathVariable Integer floorNumber,
                                                      @PathVariable String slotNumber,
                                                      @RequestBody UpdateSlotAvailabilityRequestDto request) {
        return ResponseEntity.ok(
                slotService.setSlotAvailability(lotId, floorNumber, slotNumber, request.isAvailable())
        );
    }

    @DeleteMapping("/{slotNumber}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long lotId,
                                           @PathVariable Integer floorNumber,
                                           @PathVariable String slotNumber) {
        slotService.removeSlotFromLevel(lotId, floorNumber, slotNumber);
        return ResponseEntity.noContent().build();
    }
}
