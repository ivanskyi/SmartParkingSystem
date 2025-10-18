package com.ivanskyi.smartparkingsystem.model;

import com.ivanskyi.smartparkingsystem.enumeration.SlotType;
import com.ivanskyi.smartparkingsystem.model.id.SlotId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "slot")
@Getter
@Setter
@IdClass(SlotId.class)
public class Slot {
    @Id
    @Column(name = "parking_lot_id")
    private Long parkingLotId;

    @Id
    @Column(name = "floor_number")
    private Integer floorNumber;

    @Id
    @Column(name = "slot_number")
    private String slotNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_type", nullable = false)
    private SlotType slotType;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(
                    name = "parking_lot_id",
                    referencedColumnName = "parking_lot_id",
                    insertable = false,
                    updatable = false
            ),
            @JoinColumn(
                    name = "floor_number",
                    referencedColumnName = "floor_number",
                    insertable = false,
                    updatable = false
            )
    })
    private Level level;
}
