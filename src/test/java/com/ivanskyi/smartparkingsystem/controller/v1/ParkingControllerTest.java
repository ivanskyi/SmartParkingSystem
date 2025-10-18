package com.ivanskyi.smartparkingsystem.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanskyi.smartparkingsystem.dto.SessionDto;
import com.ivanskyi.smartparkingsystem.dto.SlotDto;
import com.ivanskyi.smartparkingsystem.dto.VehicleDto;
import com.ivanskyi.smartparkingsystem.dto.request.CheckInRequestDto;
import com.ivanskyi.smartparkingsystem.dto.response.CheckInResponseDto;
import com.ivanskyi.smartparkingsystem.dto.response.CheckOutResponseDto;
import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import com.ivanskyi.smartparkingsystem.model.Session;
import com.ivanskyi.smartparkingsystem.service.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParkingController.class)
@DisplayName("Parking Controller Integration Tests")
class ParkingControllerTest {
    private static final String SESSIONS_ENDPOINT = "/v1/parking/sessions";
    private static final String CHECKOUT_ENDPOINT = "/v1/parking/sessions/{licensePlate}/checkout";
    private static final String LICENSE_PLATE = "ABC123";
    private static final String SLOT_NUMBER = "A1";
    private static final Long LOT_ID = 1L;
    private static final Integer FLOOR_NUMBER = 1;
    private static final Long SESSION_ID = 1L;
    private static final Long VEHICLE_ID = 1L;
    private static final String VEHICLE_TYPE_CAR = VehicleType.CAR.name();
    private static final BigDecimal CHECK_OUT_FEE = new BigDecimal("10.00");
    private static final BigDecimal COMPLETED_SESSION_FEE = new BigDecimal("15.00");
    private static final Long SESSION_DURATION_HOURS = 2L;
    private static final Long ACTIVE_SESSION_DURATION_HOURS = 1L;
    private static final Long COMPLETED_SESSION_DURATION_HOURS = 3L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParkingService parkingService;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("POST /v1/parking/sessions checks in vehicle and returns 201 Created with session details")
    void checkIn_should_successfully_check_in_vehicle_and_return_created_status() throws Exception {
        // Arrange
        CheckInRequestDto request = buildCheckInRequest();
        Session session = buildSession();
        CheckInResponseDto response = buildCheckInResponse();

        when(parkingService.checkIn(anyString(), anyString())).thenReturn(session);
        when(parkingService.mapToCheckInResponse(any(Session.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post(SESSIONS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vehicle.licensePlate").value(LICENSE_PLATE))
                .andExpect(jsonPath("$.vehicle.vehicleType").value(VEHICLE_TYPE_CAR))
                .andExpect(jsonPath("$.slot.slotNumber").value(SLOT_NUMBER))
                .andExpect(jsonPath("$.slot.isAvailable").value(false))
                .andExpect(jsonPath("$.checkInTime").exists());

        verify(parkingService).checkIn(anyString(), anyString());
        verify(parkingService).mapToCheckInResponse(any(Session.class));
    }

    @Test
    @DisplayName("GET /v1/parking/sessions returns all sessions when no filter applied")
    void getSessions_should_return_all_sessions_when_no_filter_applied() throws Exception {
        // Arrange
        SessionDto sessionDto = buildActiveSessionDto();
        List<SessionDto> sessions = List.of(sessionDto);

        when(parkingService.getSessions(any())).thenReturn(sessions);

        // Act & Assert
        mockMvc.perform(get(SESSIONS_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(SESSION_ID))
                .andExpect(jsonPath("$[0].vehicle.licensePlate").value(LICENSE_PLATE))
                .andExpect(jsonPath("$[0].slot.slotNumber").value(SLOT_NUMBER))
                .andExpect(jsonPath("$[0].entryTime").exists());

        verify(parkingService).getSessions(any());
    }

    @Test
    @DisplayName("GET /v1/parking/sessions returns empty list when no sessions exist")
    void getSessions_should_return_empty_list_when_no_sessions_exist() throws Exception {
        // Arrange
        when(parkingService.getSessions(any())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(SESSIONS_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(parkingService).getSessions(any());
    }

    @Test
    @DisplayName("GET /v1/parking/sessions?active=true returns only active sessions")
    void getSessions_should_return_only_active_sessions_when_active_filter_is_true() throws Exception {
        // Arrange
        SessionDto activeSession = buildActiveSessionDto();
        List<SessionDto> sessions = List.of(activeSession);

        when(parkingService.getSessions(eq(true))).thenReturn(sessions);

        // Act & Assert
        mockMvc.perform(get(SESSIONS_ENDPOINT)
                        .param("active", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].entryTime").exists())
                .andExpect(jsonPath("$[0].exitTime").doesNotExist())
                .andExpect(jsonPath("$[0].totalFee").doesNotExist());

        verify(parkingService).getSessions(eq(true));
    }

    @Test
    @DisplayName("GET /v1/parking/sessions?active=false returns only completed sessions")
    void getSessions_should_return_only_completed_sessions_when_active_filter_is_false() throws Exception {
        // Arrange
        SessionDto completedSession = buildCompletedSessionDto();
        List<SessionDto> sessions = List.of(completedSession);

        when(parkingService.getSessions(eq(false))).thenReturn(sessions);

        // Act & Assert
        mockMvc.perform(get(SESSIONS_ENDPOINT)
                        .param("active", "false")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].exitTime").exists())
                .andExpect(jsonPath("$[0].totalFee").exists());

        verify(parkingService).getSessions(eq(false));
    }

    @Test
    @DisplayName("POST /v1/parking/sessions/{licensePlate}/checkout checks out vehicle and returns 200 OK with fee details")
    void checkOut_should_successfully_check_out_vehicle_and_return_fee_details() throws Exception {
        // Arrange
        Session session = buildSession();
        CheckOutResponseDto response = buildCheckOutResponse();

        when(parkingService.checkOut(eq(LICENSE_PLATE))).thenReturn(session);
        when(parkingService.mapToCheckOutResponse(any(Session.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post(CHECKOUT_ENDPOINT, LICENSE_PLATE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vehicle.licensePlate").value(LICENSE_PLATE))
                .andExpect(jsonPath("$.slot.slotNumber").value(SLOT_NUMBER))
                .andExpect(jsonPath("$.slot.isAvailable").value(true))
                .andExpect(jsonPath("$.checkInTime").exists())
                .andExpect(jsonPath("$.checkOutTime").exists())
                .andExpect(jsonPath("$.durationHours").exists())
                .andExpect(jsonPath("$.fee").exists());

        verify(parkingService).checkOut(eq(LICENSE_PLATE));
        verify(parkingService).mapToCheckOutResponse(any(Session.class));
    }

    private CheckInRequestDto buildCheckInRequest() {
        CheckInRequestDto request = new CheckInRequestDto();
        request.setLicensePlate(LICENSE_PLATE);
        request.setVehicleType(VEHICLE_TYPE_CAR);
        return request;
    }

    private Session buildSession() {
        return new Session();
    }

    private CheckInResponseDto buildCheckInResponse() {
        return new CheckInResponseDto(
                buildVehicleDto(),
                buildSlotDto(false),
                now
        );
    }

    private CheckOutResponseDto buildCheckOutResponse() {
        LocalDateTime checkInTime = now.minusHours(SESSION_DURATION_HOURS);
        LocalDateTime checkOutTime = now;
        return new CheckOutResponseDto(
                buildVehicleDto(),
                buildSlotDto(true),
                checkInTime,
                checkOutTime,
                SESSION_DURATION_HOURS,
                CHECK_OUT_FEE
        );
    }

    private SessionDto buildActiveSessionDto() {
        SessionDto session = new SessionDto();
        session.setId(SESSION_ID);
        session.setEntryTime(now.minusHours(ACTIVE_SESSION_DURATION_HOURS));
        session.setVehicle(buildVehicleDto());
        session.setSlot(buildSlotDto(false));
        return session;
    }

    private SessionDto buildCompletedSessionDto() {
        SessionDto session = new SessionDto();
        session.setId(SESSION_ID);
        session.setEntryTime(now.minusHours(COMPLETED_SESSION_DURATION_HOURS));
        session.setExitTime(now.minusHours(ACTIVE_SESSION_DURATION_HOURS));
        session.setTotalFee(COMPLETED_SESSION_FEE);
        session.setVehicle(buildVehicleDto());
        session.setSlot(buildSlotDto(true));
        return session;
    }

    private VehicleDto buildVehicleDto() {
        VehicleDto vehicle = new VehicleDto();
        vehicle.setId(VEHICLE_ID);
        vehicle.setLicensePlate(LICENSE_PLATE);
        vehicle.setVehicleType(VehicleType.CAR);
        return vehicle;
    }

    private SlotDto buildSlotDto(boolean isAvailable) {
        SlotDto slot = new SlotDto();
        slot.setSlotNumber(SLOT_NUMBER);
        slot.setIsAvailable(isAvailable);
        slot.setParkingLotId(LOT_ID);
        slot.setFloorNumber(FLOOR_NUMBER);
        return slot;
    }
}
