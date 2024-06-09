package com.chinhbean.bookinghotel.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class BookingDTO {
    private BigDecimal totalPrice;
    private Date checkInDate;
    private Date checkOutDate;
    private Long couponId;
    private String note;
    private String paymentMethod;
    private LocalDateTime expirationDate;
    private LocalDateTime extendExpirationDate;
    private List<BookingDetailDTO> bookingDetails;
}
