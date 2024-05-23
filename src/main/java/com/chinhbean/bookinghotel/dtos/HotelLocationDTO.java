package com.chinhbean.bookinghotel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelLocationDTO {

    @JsonProperty("address")
    @NotBlank(message = "Address is required")
    private String address;

    @JsonProperty("city")
    @NotBlank(message = "City is required")
    private String city;

    @JsonProperty("district")
    @NotBlank(message = "District is required")
    private String district;
}
