package com.chinhbean.bookinghotel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TypeDTO {

    @JsonProperty("luxury")
    private Boolean luxury;
    @JsonProperty("single_bedroom")
    private Boolean singleBedroom;
    @JsonProperty("twin_bedroom")
    private Boolean twinBedroom;
    @JsonProperty("double_bedroom")
    private Boolean doubleBedroom;
}