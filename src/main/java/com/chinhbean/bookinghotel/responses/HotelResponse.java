package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("hotel_name")
    private String hotelName;

    @JsonProperty("partner")
    private UserResponse partner;

    @JsonProperty("description")
    private String description;

    @JsonProperty("rating")
    private Double rating;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("status")
    private HotelStatus status;

    @JsonProperty("location")
    private HotelLocationResponse location;

    @JsonProperty("conveniences")
    private List<ConvenienceResponse> conveniences;

//    @JsonProperty("rooms")
//    private List<RoomResponse> rooms;

    public static HotelResponse fromHotel(Hotel hotel) {
        HotelLocationResponse locationResponse = HotelLocationResponse.fromHotelLocation(hotel.getLocation());
        UserResponse partnerResponse = UserResponse.fromUser(hotel.getPartner());

        List<ConvenienceResponse> convenienceResponses = hotel.getConveniences().stream()
                .map(ConvenienceResponse::fromConvenience)
                .toList();

//        List<RoomResponse> roomResponses = (hotel.getRooms() != null) ? hotel.getRooms().stream()
//                .map(RoomResponse::fromRoom)
//                .toList() : List.of();

        return HotelResponse.builder()
                .id(hotel.getId())
                .hotelName(hotel.getHotelName())
                .partner(partnerResponse)
                .description(hotel.getDescription())
                .rating(hotel.getRating())
                .brand(hotel.getBrand())
                .status(hotel.getStatus())
                .location(locationResponse)
                .conveniences(convenienceResponses)
//                .rooms(roomResponses)
                .build();
    }
}
