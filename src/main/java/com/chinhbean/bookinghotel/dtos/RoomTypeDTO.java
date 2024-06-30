package com.chinhbean.bookinghotel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoomTypeDTO {

    @JsonProperty("hotel_id")
    private Long hotelId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("number_of_rooms")
    private Integer numberOfRooms;

    @JsonProperty("capacity_per_room")
    private int capacityPerRoom;

    @JsonProperty("room_price")
    private Double roomPrice;

    @JsonProperty("room_type_name")
    private String roomTypeName;

    @JsonProperty("types")
    private TypeRoomDTO types;

    @JsonProperty("conveniences")
    private Set<ConvenienceRoomDTO> conveniences;
}
