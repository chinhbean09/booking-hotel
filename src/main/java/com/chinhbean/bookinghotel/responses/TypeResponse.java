package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
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

    public static TypeResponse fromType(Type type) {
        return TypeResponse.builder()
                .id(type.getId())
                .doubleBedroom(type.getDoubleBedroom())
                .luxury(type.getLuxury())
                .singleBedroom(type.getSingleBedroom())
                .twinBedroom(type.getTwinBedroom())
                .wardrobe(type.getWardrobe())
                .airConditioning(type.getAirConditioning())
                .tv(type.getTv())
                .wifi(type.getWifi())
                .toiletries(type.getToiletries())
                .kitchen(type.getKitchen())
                .build();
    }
}
