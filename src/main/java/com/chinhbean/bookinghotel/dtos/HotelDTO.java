package com.chinhbean.bookinghotel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {

    @JsonProperty("hotel_name")
    @NotBlank(message = "Hotel name is required")
    private String hotelName;

    @NotNull(message = "Rating is required")
    private Double rating;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Partner ID is required")
    @JsonProperty("partner_id")
    private Long partnerId;

    @NotBlank(message = "Brand is required")
    private String brand;


    @JsonProperty("conveniences")
    private Set<ConvenienceDTO> conveniences;

    @JsonProperty("location")
    private HotelLocationDTO location;
}
