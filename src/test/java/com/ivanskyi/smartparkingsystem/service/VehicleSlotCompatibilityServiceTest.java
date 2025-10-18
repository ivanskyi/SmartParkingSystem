package com.ivanskyi.smartparkingsystem.service;

import com.ivanskyi.smartparkingsystem.enumeration.SlotType;
import com.ivanskyi.smartparkingsystem.enumeration.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VehicleSlotCompatibilityService Unit Tests")
class VehicleSlotCompatibilityServiceTest {
    private static final List<SlotType> CAR_COMPATIBLE_SLOTS = List.of(SlotType.COMPACT, SlotType.LARGE);
    private static final List<SlotType> MOTORCYCLE_COMPATIBLE_SLOTS = List.of(SlotType.MOTORCYCLE, SlotType.COMPACT);
    private static final List<SlotType> TRUCK_COMPATIBLE_SLOTS = List.of(SlotType.LARGE);

    private VehicleSlotCompatibilityService compatibilityService;

    @BeforeEach
    void setUp() {
        Map<VehicleType, List<SlotType>> compatibility = Map.ofEntries(
                Map.entry(VehicleType.CAR, CAR_COMPATIBLE_SLOTS),
                Map.entry(VehicleType.MOTORCYCLE, MOTORCYCLE_COMPATIBLE_SLOTS),
                Map.entry(VehicleType.TRUCK, TRUCK_COMPATIBLE_SLOTS)
        );
        compatibilityService = new VehicleSlotCompatibilityService(compatibility);
    }

    @Nested
    @DisplayName("getCompatibleSlots returns correct slot types")
    class GetCompatibleSlotsTests {
        @Test
        @DisplayName("CAR is compatible with COMPACT and LARGE slots")
        void getCompatibleSlots_should_return_compact_and_large_for_car() {
            // Act
            List<SlotType> compatibleSlots = compatibilityService.getCompatibleSlots(VehicleType.CAR);

            // Assert
            assertThat(compatibleSlots)
                    .containsExactlyInAnyOrderElementsOf(CAR_COMPATIBLE_SLOTS)
                    .hasSize(2);
        }

        @Test
        @DisplayName("MOTORCYCLE is compatible with MOTORCYCLE and COMPACT slots")
        void getCompatibleSlots_should_return_motorcycle_and_compact_for_motorcycle() {
            // Act
            List<SlotType> compatibleSlots = compatibilityService.getCompatibleSlots(VehicleType.MOTORCYCLE);

            // Assert
            assertThat(compatibleSlots)
                    .containsExactlyInAnyOrderElementsOf(MOTORCYCLE_COMPATIBLE_SLOTS)
                    .hasSize(2);
        }

        @Test
        @DisplayName("TRUCK is compatible with LARGE slots only")
        void getCompatibleSlots_should_return_large_for_truck() {
            // Act
            List<SlotType> compatibleSlots = compatibilityService.getCompatibleSlots(VehicleType.TRUCK);

            // Assert
            assertThat(compatibleSlots)
                    .containsExactlyInAnyOrderElementsOf(TRUCK_COMPATIBLE_SLOTS)
                    .hasSize(1);
        }

        @ParameterizedTest
        @EnumSource(VehicleType.class)
        @DisplayName("all vehicle types have defined slot compatibility")
        void getCompatibleSlots_should_return_non_empty_list_for_all_vehicle_types(VehicleType vehicleType) {
            // Act
            List<SlotType> compatibleSlots = compatibilityService.getCompatibleSlots(vehicleType);

            // Assert
            assertThat(compatibleSlots).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Edge cases and immutability")
    class EdgeCasesTests {
        @ParameterizedTest
        @EnumSource(VehicleType.class)
        @DisplayName("returned compatible slots list is unmodifiable")
        void getCompatibleSlots_should_return_unmodifiable_list(VehicleType vehicleType) {
            // Act
            List<SlotType> compatibleSlots = compatibilityService.getCompatibleSlots(vehicleType);

            // Assert
            assertThat(compatibleSlots).isUnmodifiable();
        }
    }

    @Nested
    @DisplayName("Slot type compatibility validation")
    class SlotTypeCompatibilityValidationTests {
        @Test
        @DisplayName("CAR cannot use MOTORCYCLE slots")
        void car_should_not_be_compatible_with_motorcycle_slots() {
            // Act
            List<SlotType> compatibleSlots = compatibilityService.getCompatibleSlots(VehicleType.CAR);

            // Assert
            assertThat(compatibleSlots).doesNotContain(SlotType.MOTORCYCLE);
        }

        @Test
        @DisplayName("TRUCK cannot use COMPACT or MOTORCYCLE slots")
        void truck_should_not_be_compatible_with_compact_or_motorcycle_slots() {
            // Act
            List<SlotType> compatibleSlots = compatibilityService.getCompatibleSlots(VehicleType.TRUCK);

            // Assert
            assertThat(compatibleSlots)
                    .doesNotContain(SlotType.COMPACT, SlotType.MOTORCYCLE);
        }

        @Test
        @DisplayName("MOTORCYCLE can use COMPACT as fallback")
        void motorcycle_should_have_compact_as_fallback() {
            // Act
            List<SlotType> compatibleSlots = compatibilityService.getCompatibleSlots(VehicleType.MOTORCYCLE);

            // Assert
            assertThat(compatibleSlots).contains(SlotType.COMPACT);
        }

        @Test
        @DisplayName("CAR has LARGE as fallback when COMPACT unavailable")
        void car_should_have_large_as_fallback() {
            // Act
            List<SlotType> compatibleSlots = compatibilityService.getCompatibleSlots(VehicleType.CAR);

            // Assert
            assertThat(compatibleSlots).contains(SlotType.LARGE);
        }
    }
}
