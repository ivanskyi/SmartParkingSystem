package com.ivanskyi.smartparkingsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class LotDto {
    private Long id;
    private String name;
    private List<LevelDto> levels;
}
