package com.chinhbean.bookinghotel.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelFilterDTO {
    private Integer rating;
    private Boolean freeBreakfast;
    private Boolean pickUpDropOff;
    private Boolean restaurant;
    private Boolean bar;
    private Boolean pool;
    private Boolean freeInternet;
    private Boolean reception24h;
    private Boolean laundry;
    private int page;
    private int size;
}