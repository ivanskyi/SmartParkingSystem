package com.ivanskyi.smartparkingsystem.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanskyi.smartparkingsystem.dto.LevelDto;
import com.ivanskyi.smartparkingsystem.service.LevelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LevelController.class)
@DisplayName("Level Controller Integration Tests")
class LevelControllerTest {
    private static final Long VALID_LOT_ID = 1L;
    private static final Integer VALID_FLOOR_NUMBER = 1;
    private static final String GROUND_FLOOR_DESCRIPTION = "Ground Floor";
    private static final String LEVELS_ENDPOINT = "/v1/lots/{lotId}/levels";
    private static final String LEVEL_BY_FLOOR_ENDPOINT = "/v1/lots/{lotId}/levels/{floorNumber}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LevelService levelService;

    @Autowired
    private ObjectMapper objectMapper;

    private LevelDto validLevelDto;

    @BeforeEach
    void setUp() {
        validLevelDto = createValidLevelDto();
    }

    @Test
    @DisplayName("GET /v1/lots/{lotId}/levels returns list of all levels for specific lot")
    void getAllLevels_should_return_all_levels_when_endpoint_called() throws Exception {
        // Arrange
        List<LevelDto> expectedLevels = List.of(validLevelDto);
        when(levelService.getAllByLotId(eq(VALID_LOT_ID))).thenReturn(expectedLevels);

        // Act & Assert
        mockMvc.perform(get(LEVELS_ENDPOINT, VALID_LOT_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].parkingLotId").value(validLevelDto.getParkingLotId()))
                .andExpect(jsonPath("$[0].floorNumber").value(validLevelDto.getFloorNumber()))
                .andExpect(jsonPath("$[0].description").value(validLevelDto.getDescription()));

        verify(levelService).getAllByLotId(eq(VALID_LOT_ID));
    }

    @Test
    @DisplayName("GET /v1/lots/{lotId}/levels returns empty list when no levels exist")
    void getAllLevels_should_return_empty_list_when_no_levels_exist() throws Exception {
        // Arrange
        when(levelService.getAllByLotId(eq(VALID_LOT_ID))).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(LEVELS_ENDPOINT, VALID_LOT_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /v1/lots/{lotId}/levels creates new level and returns 201 Created")
    void addLevel_should_create_new_level_and_return_created_status() throws Exception {
        // Arrange
        when(levelService.add(eq(VALID_LOT_ID), any(LevelDto.class))).thenReturn(validLevelDto);

        // Act & Assert
        mockMvc.perform(post(LEVELS_ENDPOINT, VALID_LOT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLevelDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.parkingLotId").value(validLevelDto.getParkingLotId()))
                .andExpect(jsonPath("$.floorNumber").value(validLevelDto.getFloorNumber()))
                .andExpect(jsonPath("$.description").value(validLevelDto.getDescription()));

        verify(levelService).add(eq(VALID_LOT_ID), any(LevelDto.class));
    }

    @Test
    @DisplayName("GET /v1/lots/{lotId}/levels/{floorNumber} returns specific level with valid IDs")
    void getLevel_should_return_level_when_valid_ids_provided() throws Exception {
        // Arrange
        when(levelService.getById(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER))).thenReturn(validLevelDto);

        // Act & Assert
        mockMvc.perform(get(LEVEL_BY_FLOOR_ENDPOINT, VALID_LOT_ID, VALID_FLOOR_NUMBER)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.parkingLotId").value(validLevelDto.getParkingLotId()))
                .andExpect(jsonPath("$.floorNumber").value(validLevelDto.getFloorNumber()))
                .andExpect(jsonPath("$.description").value(validLevelDto.getDescription()));

        verify(levelService).getById(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER));
    }

    @Test
    @DisplayName("DELETE /v1/lots/{lotId}/levels/{floorNumber} deletes level and returns 204 No Content")
    void removeLevel_should_delete_level_and_return_no_content_status() throws Exception {

        // Act & Assert
        mockMvc.perform(delete(LEVEL_BY_FLOOR_ENDPOINT, VALID_LOT_ID, VALID_FLOOR_NUMBER))
                .andExpect(status().isNoContent());

        verify(levelService).remove(eq(VALID_LOT_ID), eq(VALID_FLOOR_NUMBER));
    }

    private LevelDto createValidLevelDto() {
        LevelDto dto = new LevelDto();
        dto.setParkingLotId(VALID_LOT_ID);
        dto.setFloorNumber(VALID_FLOOR_NUMBER);
        dto.setDescription(GROUND_FLOOR_DESCRIPTION);
        dto.setSlots(Collections.emptyList());
        return dto;
    }
}
