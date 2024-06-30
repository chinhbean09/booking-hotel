package com.chinhbean.bookinghotel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BookingDetailDTO {
    @JsonProperty("room-type-id")
    private Long roomTypeId;
    private BigDecimal price;
    @JsonProperty("number-of-rooms")
    private Integer numberOfRooms;
    @JsonProperty("total-money")
    private BigDecimal totalMoney;
}
