package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypeResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("double_bedroom")
    private Boolean doubleBedroom;

    @JsonProperty("luxury")
    private Boolean luxury;

    @JsonProperty("single_bedroom")
    private Boolean singleBedroom;

    @JsonProperty("twin_bedroom")
    private Boolean twinBedroom;

    public static TypeResponse fromType(Type type) {
        return TypeResponse.builder()
                .id(type.getId())
                .doubleBedroom(type.getDoubleBedroom())
                .luxury(type.getLuxury())
                .singleBedroom(type.getSingleBedroom())
                .twinBedroom(type.getTwinBedroom())
                .build();
    }
}
