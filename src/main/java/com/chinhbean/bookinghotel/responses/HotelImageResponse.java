package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.HotelImages;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelImageResponse {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("hotel_id")
    private Long hotelId;

    public static HotelImageResponse fromHotelImage(HotelImages hotelImage) {
        return HotelImageResponse.builder()
                .id(hotelImage.getId())
                .imageUrl(hotelImage.getImageUrl())
                .hotelId(hotelImage.getHotel().getId())
                .build();
    }
}
