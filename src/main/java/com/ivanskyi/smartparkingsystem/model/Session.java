package com.ivanskyi.smartparkingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_session",
        indexes = {
                @Index(
                        name = "active_sessions",
                        columnList = "vehicle_id, exit_time"
                ),
                @Index(
                        name = "slot_sessions",
                        columnList = "parking_lot_id, floor_number, slot_number, exit_time"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parking_lot_id", nullable = false)
    private Long parkingLotId;

    @Column(name = "floor_number", nullable = false)
    private Integer floorNumber;

    @Column(name = "slot_number", nullable = false)
    private String slotNumber;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime = LocalDateTime.now();

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @Column(name = "total_fee", precision = 10, scale = 2)
    private BigDecimal totalFee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(
                    name = "parking_lot_id",
                    referencedColumnName = "parking_lot_id",
                    insertable = false, updatable = false
            ),
            @JoinColumn(
                    name = "floor_number",
                    referencedColumnName = "floor_number",
                    insertable = false,
                    updatable = false
            ),
            @JoinColumn(
                    name = "slot_number",
                    referencedColumnName = "slot_number",
                    insertable = false,
                    updatable = false
            )
    })
    private Slot slot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
}
