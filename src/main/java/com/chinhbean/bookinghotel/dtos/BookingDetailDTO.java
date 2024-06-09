package com.chinhbean.bookinghotel.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BookingDetailDTO {
    private Long roomTypeId;
    private BigDecimal price;
    private Integer numberOfRooms;
    private BigDecimal totalMoney;
}
