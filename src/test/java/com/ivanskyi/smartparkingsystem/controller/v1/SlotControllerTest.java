package com.ivanskyi.smartparkingsystem.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanskyi.smartparkingsystem.dto.SlotDto;
import com.ivanskyi.smartparkingsystem.dto.request.CreateSlotRequestDto;
import com.ivanskyi.smartparkingsystem.dto.request.UpdateSlotAvailabilityRequestDto;
import com.ivanskyi.smartparkingsystem.service.SlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SlotController.class)
@DisplayName("Slot Controller Integration Tests")
class SlotControllerTest {
    private static final Long VALID_LOT_ID = 1L;
    private static final Integer VALID_FLOOR_NUMBER = 2;
    private static final String VALID_SLOT_NUMBER = "A1";
    private static final Long SLOT_NUMBER_ID = 1L;
    private static final Boolean SLOT_AVAILABLE = true;
    private static final String SLOTS_ENDPOINT = "/v1/lots/{lotId}/levels/{floorNumber}/slots";
    private static final String SLOT_BY_NUMBER_ENDPOINT = "/v1/lots/{lotId}/levels/{floorNumber}/slots/{slotNumber}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SlotService slotService;

    private SlotDto validSlotDto;
    private CreateSlotRequestDto createSlotRequest;
    private UpdateSlotAvailabilityRequestDto updateAvailabilityRequest;

    @BeforeEach
    void setUp() {
        validSlotDto = createValidSlotDto();
        createSlotRequest = createValidCreateSlotRequest();
        updateAvailabilityRequest = createValidUpdateAvailabilityRequest();
    }

    @Test
    @DisplayName("GET /v1/lots/{lotId}/levels/{floorNumber}/slots returns list of all slots")
    void getSlots_should_return_all_slots_when_endpoint_called() throws Exception {
        // Arrange
        List<SlotDto> expectedSlots = List.of(validSlotDto);
        when(slotService.getSlots(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER))).thenReturn(expectedSlots);

        // Act & Assert
        mockMvc.perform(get(SLOTS_ENDPOINT, VALID_LOT_ID, VALID_FLOOR_NUMBER)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].slotNumber").value(validSlotDto.getSlotNumber()))
                .andExpect(jsonPath("$[0].isAvailable").value(validSlotDto.getIsAvailable()));

        verify(slotService).getSlots(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER));
    }

    @Test
    @DisplayName("GET /v1/lots/{lotId}/levels/{floorNumber}/slots returns empty list when no slots exist")
    void getSlots_should_return_empty_list_when_no_slots_exist() throws Exception {
        // Arrange
        when(slotService.getSlots(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER))).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(SLOTS_ENDPOINT, VALID_LOT_ID, VALID_FLOOR_NUMBER)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /v1/lots/{lotId}/levels/{floorNumber}/slots creates new slot and returns 201 Created")
    void createSlot_should_create_new_slot_and_return_created_status() throws Exception {
        // Arrange
        when(slotService.addSlotToLevel(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER), any(CreateSlotRequestDto.class)))
                .thenReturn(validSlotDto);

        // Act & Assert
        mockMvc.perform(post(SLOTS_ENDPOINT, VALID_LOT_ID, VALID_FLOOR_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSlotRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slotNumber").value(validSlotDto.getSlotNumber()))
                .andExpect(jsonPath("$.isAvailable").value(validSlotDto.getIsAvailable()));

        verify(slotService).addSlotToLevel(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER), any(CreateSlotRequestDto.class));
    }

    @Test
    @DisplayName("GET /v1/lots/{lotId}/levels/{floorNumber}/slots/{slotNumber} returns specific slot with valid parameters")
    void getSlot_should_return_slot_when_valid_parameters_provided() throws Exception {
        // Arrange
        when(slotService.getSlot(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER), eq(VALID_SLOT_NUMBER)))
                .thenReturn(validSlotDto);

        // Act & Assert
        mockMvc.perform(get(SLOT_BY_NUMBER_ENDPOINT, VALID_LOT_ID, VALID_FLOOR_NUMBER, VALID_SLOT_NUMBER)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slotNumber").value(validSlotDto.getSlotNumber()))
                .andExpect(jsonPath("$.isAvailable").value(validSlotDto.getIsAvailable()));

        verify(slotService).getSlot(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER), eq(VALID_SLOT_NUMBER));
    }

    @Test
    @DisplayName("PATCH /v1/lots/{lotId}/levels/{floorNumber}/slots/{slotNumber} updates slot availability and returns 200 OK")
    void updateSlot_should_update_slot_availability_and_return_updated_slot() throws Exception {
        // Arrange
        when(slotService.setSlotAvailability(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER), eq(VALID_SLOT_NUMBER), anyBoolean()))
                .thenReturn(validSlotDto);

        // Act & Assert
        mockMvc.perform(patch(SLOT_BY_NUMBER_ENDPOINT, VALID_LOT_ID, VALID_FLOOR_NUMBER, VALID_SLOT_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAvailabilityRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slotNumber").value(validSlotDto.getSlotNumber()))
                .andExpect(jsonPath("$.isAvailable").value(validSlotDto.getIsAvailable()));

        verify(slotService).setSlotAvailability(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER), eq(VALID_SLOT_NUMBER), anyBoolean());
    }

    private SlotDto createValidSlotDto() {
        SlotDto dto = new SlotDto();
        dto.setSlotNumber(VALID_SLOT_NUMBER);
        dto.setIsAvailable(SLOT_AVAILABLE);
        dto.setParkingLotId(VALID_LOT_ID);
        dto.setFloorNumber(VALID_FLOOR_NUMBER);
        return dto;
    }

    private CreateSlotRequestDto createValidCreateSlotRequest() {
        CreateSlotRequestDto dto = new CreateSlotRequestDto();
        dto.setSlotNumber(SLOT_NUMBER_ID);
        return dto;
    }

    private UpdateSlotAvailabilityRequestDto createValidUpdateAvailabilityRequest() {
        UpdateSlotAvailabilityRequestDto dto = new UpdateSlotAvailabilityRequestDto();
        dto.setAvailable(SLOT_AVAILABLE);
        return dto;
    }
}
