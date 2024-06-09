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
    private Boolean luxury = false;
    @JsonProperty("single_bedroom")
    private Boolean singleBedroom = false;
    @JsonProperty("twin_bedroom")
    private Boolean twinBedroom = false;
    @JsonProperty("double_bedroom")
    private Boolean doubleBedroom = false;
}