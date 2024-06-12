package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.HotelLocation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelLocationResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("address")
    private String address;

    @JsonProperty("province")
    private String province;

    public static HotelLocationResponse fromHotelLocation(HotelLocation hotelLocation) {
        return HotelLocationResponse.builder()
                .id(hotelLocation.getId())
                .address(hotelLocation.getAddress())
                .province(hotelLocation.getProvince())
                .build();
    }
}
