package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @JsonProperty("roomTypes")
    private List<RoomTypeResponse> roomTypes;

    @JsonProperty("feedbacks")
    private List<FeedbackResponse> feedbacks;

    @JsonProperty("image_urls")
    private List<HotelImageResponse> imageUrls;

    @JsonProperty("business_license")
    private String businessLicense;

    public static HotelResponse fromHotel(Hotel hotel) {
        HotelLocationResponse locationResponse = (hotel.getLocation() != null) ? HotelLocationResponse.fromHotelLocation(hotel.getLocation()) : null;
        UserResponse partnerResponse = (hotel.getPartner() != null) ? UserResponse.fromUser(hotel.getPartner()) : null;

        List<ConvenienceResponse> convenienceResponses = Optional.ofNullable(hotel.getConveniences())
                .orElseGet(Collections::emptySet)
                .stream()
                .map(ConvenienceResponse::fromConvenience)
                .collect(Collectors.toList());

        List<RoomTypeResponse> roomTypeResponses = Optional.ofNullable(hotel.getRoomTypes())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(RoomTypeResponse::fromType)
                .collect(Collectors.toList());

        List<FeedbackResponse> feedbackResponses = Optional.ofNullable(hotel.getFeedbacks())
                .orElseGet(Collections::emptySet)
                .stream()
                .map(FeedbackResponse::fromFeedback)
                .collect(Collectors.toList());

        List<HotelImageResponse> hotelImageResponses = Optional.ofNullable(hotel.getHotelImages())
                .orElseGet(Collections::emptySet)
                .stream()
                .map(HotelImageResponse::fromHotelImage)
                .collect(Collectors.toList());

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
                .roomTypes(roomTypeResponses)
                .feedbacks(feedbackResponses)
                .imageUrls(hotelImageResponses)
                .businessLicense(hotel.getBusinessLicense())
                .build();
    }
}
