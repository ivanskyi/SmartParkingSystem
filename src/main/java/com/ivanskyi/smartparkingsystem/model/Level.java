package com.ivanskyi.smartparkingsystem.model;

import com.ivanskyi.smartparkingsystem.model.id.LevelId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "level")
@Getter
@Setter
@IdClass(LevelId.class)
public class Level {
    @Id
    @Column(name = "parking_lot_id")
    private Long parkingLotId;

    @Id
    @Column(name = "floor_number")
    private Integer floorNumber;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_lot_id", insertable = false, updatable = false)
    private Lot lot;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Slot> slots;
}
