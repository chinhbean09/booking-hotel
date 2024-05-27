package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.TypeRoom;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomTypeResponse {
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

    public static RoomTypeResponse fromType(TypeRoom typeRoom) {
        return RoomTypeResponse.builder()
                .id(typeRoom.getId())
                .doubleBedroom(typeRoom.getDoubleBedroom())
                .luxury(typeRoom.getLuxury())
                .singleBedroom(typeRoom.getSingleBedroom())
                .twinBedroom(typeRoom.getTwinBedroom())
                .build();
    }
}
