package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.HotelLocation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelLocationResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("address")
    private String address;

    @JsonProperty("city")
    private String city;

    @JsonProperty("district")
    private String district;

    public static HotelLocationResponse fromHotelLocation(HotelLocation hotelLocation) {
        return HotelLocationResponse.builder()
                .id(hotelLocation.getId())
                .address(hotelLocation.getAddress())
                .city(hotelLocation.getCity())
                .district(hotelLocation.getDistrict())
                .build();
    }
}
