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

    @JsonProperty("url")
    private String url;

    public static HotelImageResponse fromHotelImage(HotelImages hotelImages) {
        return HotelImageResponse.builder()
                .id(hotelImages.getId())
                .url(hotelImages.getUrl())
                .build();
    }
}
