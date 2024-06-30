package com.chinhbean.bookinghotel.responses.room;

import com.chinhbean.bookinghotel.entities.RoomType;
import com.chinhbean.bookinghotel.entities.Type;
import com.chinhbean.bookinghotel.enums.RoomTypeStatus;
import com.chinhbean.bookinghotel.responses.type.TypeResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomTypeResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("hotel_id")
    private Long hotelId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("capacity_per_room")
    private int capacityPerRoom;

    @JsonProperty("number_of_rooms")
    private Integer numberOfRooms;

    @JsonProperty("room_price")
    private Double roomPrice;

    @JsonProperty("room_type_name")
    private String roomTypeName;

    @JsonProperty("status")
    private RoomTypeStatus status;

    @JsonProperty("image_urls")
    private List<RoomImageResponse> imageUrls;

    @JsonProperty("types")
    private List<TypeResponse> types;

    private List<ConvenienceRoomResponse> conveniences;

    public static RoomTypeResponse fromType(RoomType roomType) {

        List<TypeResponse> types = Arrays.stream(new Type[]{roomType.getType()})
                .map(TypeResponse::fromType)
                .toList();

        List<RoomImageResponse> imageUrls = roomType.getRoomImages().stream()
                .map(RoomImageResponse::fromRoomImage)
                .toList();

        List<ConvenienceRoomResponse> conveniences = roomType.getRoomConveniences().stream()
                .map(ConvenienceRoomResponse::fromConvenienceRoom)
                .toList();

        return RoomTypeResponse.builder()
                .id(roomType.getId())
                .hotelId(roomType.getHotel().getId())
                .roomTypeName(roomType.getRoomTypeName())
                .description(roomType.getDescription())
                .capacityPerRoom(roomType.getCapacityPerRoom())
                .numberOfRooms(roomType.getNumberOfRoom())
                .roomPrice(roomType.getRoomPrice())
                .status(roomType.getStatus())
                .imageUrls(imageUrls)
                .conveniences(conveniences)
                .types(types)
                .build();
    }
}
