package com.chinhbean.bookinghotel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BookingDTO {
    @JsonProperty("total-price")
    private BigDecimal totalPrice;

    @JsonProperty("check-in-date")
    private LocalDate checkInDate;

    @JsonProperty("check-out-date")
    private LocalDate checkOutDate;

    @JsonProperty("coupon-id")
    private Long couponId;

    @JsonProperty("user-id")
    private Long userId;

    @JsonProperty("full-name")
    private String fullName;

    @JsonProperty("phone-number")
    private Long phoneNumber;

    @JsonProperty("email")
    private String email;

    private String note;

    @JsonProperty("booking-date")
    private LocalDateTime bookingDate;

    @JsonProperty("payment-method")
    private String paymentMethod;

    @JsonProperty("booking-details")
    private List<BookingDetailDTO> bookingDetails;

}
