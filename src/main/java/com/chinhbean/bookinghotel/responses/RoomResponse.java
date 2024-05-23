package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.Room;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("availability")
    private String availability;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("types")
    private List<TypeResponse> types;

    public static RoomResponse fromRoom(Room room) {
        List<TypeResponse> typeResponses = room.getTypes().stream()
                .map(TypeResponse::fromType)
                .collect(Collectors.toList());

        return RoomResponse.builder()
                .id(room.getId())
                .availability(room.getAvailability())
                .price(room.getPrice())
                .types(typeResponses)
                .build();
    }
}