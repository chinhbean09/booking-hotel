package com.chinhbean.bookinghotel.dtos;

import com.chinhbean.bookinghotel.validators.AtLeastOneTrue;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@AtLeastOneTrue
public class ConvenienceDTO {

    @JsonProperty("free_breakfast")
    private Boolean freeBreakfast = false;

    @JsonProperty("pick_up_drop_off")
    private Boolean pickUpDropOff = false;

    @JsonProperty("restaurant")
    private Boolean restaurant = false;

    @JsonProperty("bar")
    private Boolean bar = false;

    @JsonProperty("pool")
    private Boolean pool = false;

    @JsonProperty("free_internet")
    private Boolean freeInternet = false;

    @JsonProperty("reception_24h")
    private Boolean reception24h = false;

    @JsonProperty("laundry")
    private Boolean laundry = false;
}
