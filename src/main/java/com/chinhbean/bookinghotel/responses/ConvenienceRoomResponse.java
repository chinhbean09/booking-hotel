package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.ConvenienceRoom;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConvenienceRoomResponse {

    @JsonProperty("id")
    private Long id;

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

    public static ConvenienceRoomResponse fromConvenienceRoom(ConvenienceRoom convenienceRoom) {
        return ConvenienceRoomResponse.builder()
                .id(convenienceRoom.getId())
                .wardrobe(convenienceRoom.getWardrobe())
                .airConditioning(convenienceRoom.getAirConditioning())
                .tv(convenienceRoom.getTv())
                .wifi(convenienceRoom.getWifi())
                .toiletries(convenienceRoom.getToiletries())
                .kitchen(convenienceRoom.getKitchen())
                .build();
    }
}
