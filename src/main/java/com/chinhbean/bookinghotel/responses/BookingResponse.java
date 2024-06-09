package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.entities.Booking;
import com.chinhbean.bookinghotel.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    @JsonProperty("bookingId")
    private Long bookingId;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("totalPrice")
    private BigDecimal totalPrice;

    @JsonProperty("checkInDate")
    private Date checkInDate;

    @JsonProperty("checkOutDate")
    private Date checkOutDate;

    @JsonProperty("status")
    private BookingStatus status;

    @JsonProperty("couponId")
    private Long couponId;

    @JsonProperty("note")
    private String note;

    @JsonProperty("bookingDate")
    private Date bookingDate;

    @JsonProperty("paymentMethod")
    private String paymentMethod;

    @JsonProperty("expirationDate")
    private LocalDateTime expirationDate;

    @JsonProperty("extendExpirationDate")
    private LocalDateTime extendExpirationDate;

    public static BookingResponse fromBooking(Booking booking) {
        UserResponse userResponse = UserResponse.fromUser(booking.getUser());

        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .user(userResponse)
                .totalPrice(booking.getTotalPrice())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .status(booking.getStatus())
                .couponId(booking.getCouponId())
                .note(booking.getNote())
                .bookingDate(booking.getBookingDate())
                .paymentMethod(booking.getPaymentMethod())
                .expirationDate(booking.getExpirationDate())
                .extendExpirationDate(booking.getExtendExpirationDate())
                .build();
    }
}
