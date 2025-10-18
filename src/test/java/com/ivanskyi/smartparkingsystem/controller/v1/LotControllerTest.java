package com.ivanskyi.smartparkingsystem.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanskyi.smartparkingsystem.dto.LotDto;
import com.ivanskyi.smartparkingsystem.service.LotService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LotController.class)
@DisplayName("Lot Controller Integration Tests")
class LotControllerTest {
    private static final Long VALID_LOT_ID = 1L;
    private static final String TEST_LOT_NAME = "Test Parking Lot";
    private static final String LOTS_ENDPOINT = "/v1/lots";
    private static final String LOT_BY_ID_ENDPOINT = "/v1/lots/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LotService lotService;

    @Autowired
    private ObjectMapper objectMapper;

    private LotDto validLotDto;

    @BeforeEach
    void setUp() {
        validLotDto = createValidLotDto();
    }

    @Test
    @DisplayName("GET /v1/lots returns list of all parking lots")
    void getAllLots_should_return_all_lots_when_endpoint_called() throws Exception {
        // Arrange
        List<LotDto> expectedLots = List.of(validLotDto);
        when(lotService.getAll()).thenReturn(expectedLots);

        // Act & Assert
        mockMvc.perform(get(LOTS_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(validLotDto.getId()))
                .andExpect(jsonPath("$[0].name").value(validLotDto.getName()));

        verify(lotService).getAll();
    }

    @Test
    @DisplayName("GET /v1/lots returns empty list when no lots exist")
    void getAllLots_should_return_empty_list_when_no_lots_exist() throws Exception {
        // Arrange
        when(lotService.getAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(LOTS_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /v1/lots creates new lot and returns 201 Created")
    void createLot_should_create_new_lot_and_return_created_status() throws Exception {
        // Arrange
        when(lotService.create(any(LotDto.class))).thenReturn(validLotDto);

        // Act & Assert
        mockMvc.perform(post(LOTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLotDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(validLotDto.getId()))
                .andExpect(jsonPath("$.name").value(validLotDto.getName()));

        verify(lotService).create(any(LotDto.class));
    }

    @Test
    @DisplayName("GET /v1/lots/{id} returns specific lot with valid ID")
    void getLot_should_return_lot_when_valid_id_provided() throws Exception {
        // Arrange
        when(lotService.getById(eq(VALID_LOT_ID))).thenReturn(validLotDto);

        // Act & Assert
        mockMvc.perform(get(LOT_BY_ID_ENDPOINT, VALID_LOT_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(validLotDto.getId()))
                .andExpect(jsonPath("$.name").value(validLotDto.getName()));

        verify(lotService).getById(eq(VALID_LOT_ID));
    }

    @Test
    @DisplayName("DELETE /v1/lots/{id} deletes lot and returns 204 No Content")
    void deleteLot_should_delete_lot_and_return_no_content_status() throws Exception {

        // Act & Assert
        mockMvc.perform(delete(LOT_BY_ID_ENDPOINT, VALID_LOT_ID))
                .andExpect(status().isNoContent());

        verify(lotService).delete(eq(VALID_LOT_ID));
    }

    private LotDto createValidLotDto() {
        LotDto dto = new LotDto();
        dto.setId(VALID_LOT_ID);
        dto.setName(TEST_LOT_NAME);
        dto.setLevels(Collections.emptyList());
        return dto;
    }
}
