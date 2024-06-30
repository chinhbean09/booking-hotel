package com.chinhbean.bookinghotel.responses.hotel;

import com.chinhbean.bookinghotel.entities.HotelBusinessLicense;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelBusinessLicenseResponse {
    @JsonProperty("business_license_url")
    private String businessLicenseUrl;


    public static HotelBusinessLicenseResponse fromHotelBusinessLicense(HotelBusinessLicense hotelBusinessLicense) {
        return HotelBusinessLicenseResponse.builder()
                .businessLicenseUrl(hotelBusinessLicense.getBusinessLicense())
                .build();
    }
}
