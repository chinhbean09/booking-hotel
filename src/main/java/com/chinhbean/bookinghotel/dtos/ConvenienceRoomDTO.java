package com.chinhbean.bookinghotel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvenienceRoomDTO {
    @JsonProperty("wardrobe")
    private Boolean wardrobe = false;
    @JsonProperty("air_conditioning")
    private Boolean airConditioning = false;
    @JsonProperty("tv")
    private Boolean tv = false;
    @JsonProperty("wifi")
    private Boolean wifi = false;
    @JsonProperty("toiletries")
    private Boolean toiletries = false;
    @JsonProperty("kitchen")
    private Boolean kitchen = false;
}
