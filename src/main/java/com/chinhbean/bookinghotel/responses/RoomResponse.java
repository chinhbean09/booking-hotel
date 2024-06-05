package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.Room;
import com.chinhbean.bookinghotel.entities.RoomImage;
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

    @JsonProperty("room_number")
    private String roomNumber;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("image_urls")
    private List<RoomImageResponse> imageUrls; // New field for image URLs

    @JsonProperty("types")
    private List<RoomTypeResponse> types;

    @JsonProperty("conveniences")
    private List<ConvenienceRoomResponse> conveniences;

    public static RoomResponse fromRoom(Room room) {
        List<RoomTypeResponse> typeRoomRespons = room.getTypes().stream()
                .map(RoomTypeResponse::fromType)
                .collect(Collectors.toList());

        List<ConvenienceRoomResponse> convenienceRoomRespons = room.getRoomConveniences().stream()
                .map(ConvenienceRoomResponse::fromConvenienceRoom)
                .collect(Collectors.toList());

        List<RoomImageResponse> imageUrls = room.getRoomImages().stream()
                .map(RoomImageResponse::fromRoomImage)
                .toList();

        return RoomResponse.builder()
                .id(room.getId())
                .availability(room.getAvailability())
                .roomNumber(room.getRoomNumber())
                .price(room.getPrice())
                .imageUrls(imageUrls)
                .types(typeRoomRespons)
                .conveniences(convenienceRoomRespons)
                .build();
    }
}