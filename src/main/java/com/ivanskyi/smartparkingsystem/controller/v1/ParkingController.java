package com.ivanskyi.smartparkingsystem.controller.v1;

import com.ivanskyi.smartparkingsystem.dto.SessionDto;
import com.ivanskyi.smartparkingsystem.dto.request.CheckInRequestDto;
import com.ivanskyi.smartparkingsystem.dto.response.CheckInResponseDto;
import com.ivanskyi.smartparkingsystem.dto.response.CheckOutResponseDto;
import com.ivanskyi.smartparkingsystem.model.Session;
import com.ivanskyi.smartparkingsystem.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/parking/sessions")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping
    public ResponseEntity<CheckInResponseDto> checkIn(@RequestBody CheckInRequestDto request) {
        Session session = parkingService.checkIn(request.getLicensePlate(), request.getVehicleType());
        CheckInResponseDto response = parkingService.mapToCheckInResponse(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SessionDto>> getSessions(@RequestParam(required = false) Boolean active) {
        List<SessionDto> sessions = parkingService.getSessions(active);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/{licensePlate}/checkout")
    public ResponseEntity<CheckOutResponseDto> checkOut(@PathVariable String licensePlate) {
        Session session = parkingService.checkOut(licensePlate);
        CheckOutResponseDto response = parkingService.mapToCheckOutResponse(session);
        return ResponseEntity.ok(response);
    }
}
