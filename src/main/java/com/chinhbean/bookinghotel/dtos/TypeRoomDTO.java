package com.chinhbean.bookinghotel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TypeRoomDTO {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("luxury")
    private Boolean luxury;
    @JsonProperty("single_bedroom")
    private Boolean singleBedroom;
    @JsonProperty("twin_bedroom")
    private Boolean twinBedroom;
    @JsonProperty("double_bedroom")
    private Boolean doubleBedroom;
    @JsonProperty("wardrobe")
    private Boolean wardrobe;
    @JsonProperty("air_conditioning")
    private Boolean airConditioning;
    @JsonProperty("tv")
    private Boolean tv;
    @JsonProperty("wifi")
    private Boolean wifi;
    @JsonProperty("toiletries")
    private Boolean toiletries;
    @JsonProperty("kitchen")
    private Boolean kitchen;
}