package com.chinhbean.bookinghotel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {

    @JsonProperty("hotel_id")
    @NotBlank(message = "hotel id is required")
    private long hotelId;

    @JsonProperty("room_number")
    @NotBlank(message = "room number is required")
    private String roomNumber;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("availability")
    private String availability;

    @JsonProperty("types")
    private Set<TypeRoomDTO> types;

    @JsonProperty("conveniences")
    private Set<ConvenienceRoomDTO> conveniences;
}