package com.chinhbean.bookinghotel.responses;

import com.chinhbean.bookinghotel.dtos.BookingDetailDTO;
import com.chinhbean.bookinghotel.entities.Booking;
import com.chinhbean.bookinghotel.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    @JsonProperty("booking-id")
    private Long bookingId;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("total-price")
    private BigDecimal totalPrice;

    @JsonProperty("check-in-date")
    private LocalDate checkInDate;

    @JsonProperty("check-out-date")
    private LocalDate checkOutDate;

    @JsonProperty("status")
    private BookingStatus status;

    @JsonProperty("coupon-id")
    private Long couponId;

    @JsonProperty("note")
    private String note;

    @JsonProperty("booking-date")
    private LocalDateTime bookingDate;

    @JsonProperty("payment-method")
    private String paymentMethod;

    @JsonProperty("expiration-date")
    private LocalDateTime expirationDate;

    @JsonProperty("booking-details")
    private List<BookingDetailDTO> bookingDetails;

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
                .bookingDetails(booking.getBookingDetails().stream()
                        .map(BookingDetailDTO::fromBookingDetail)
                        .toList())
                .build();
    }
}
