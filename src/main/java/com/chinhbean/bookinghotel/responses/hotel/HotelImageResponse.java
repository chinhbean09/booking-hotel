package com.chinhbean.bookinghotel.responses.hotel;

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

    @JsonProperty("url")
    private String imageUrl;

    @JsonProperty
    private Long hotelId;


    public static HotelImageResponse fromHotelImage(HotelImages hotelImages) {
        return HotelImageResponse.builder()
                .id(hotelImages.getId())
                .imageUrl(hotelImages.getImageUrl())
                .build();
    }
}
