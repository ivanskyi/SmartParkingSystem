package com.ivanskyi.smartparkingsystem.model.id;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LevelId implements Serializable {
    private Long parkingLotId;
    private Integer floorNumber;
}
