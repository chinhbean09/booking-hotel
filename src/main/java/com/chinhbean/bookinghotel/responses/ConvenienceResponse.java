package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.Convenience;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConvenienceResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("bar")
    private Boolean bar;

    @JsonProperty("free_breakfast")
    private Boolean freeBreakfast;

    @JsonProperty("free_internet")
    private Boolean freeInternet;

    @JsonProperty("laundry")
    private Boolean laundry;

    @JsonProperty("pick_up_drop_off")
    private Boolean pickUpDropOff;

    @JsonProperty("pool")
    private Boolean pool;

    @JsonProperty("reception_24h")
    private Boolean reception24h;

    @JsonProperty("restaurant")
    private Boolean restaurant;

    public static ConvenienceResponse fromConvenience(Convenience convenience) {
        return ConvenienceResponse.builder()
                .id(convenience.getId())
                .bar(convenience.getBar())
                .freeBreakfast(convenience.getFreeBreakfast())
                .freeInternet(convenience.getFreeInternet())
                .laundry(convenience.getLaundry())
                .pickUpDropOff(convenience.getPickUpDropOff())
                .pool(convenience.getPool())
                .reception24h(convenience.getReception24h())
                .restaurant(convenience.getRestaurant())
                .build();
    }
}
