package com.chinhbean.bookinghotel.controllers;

import com.chinhbean.bookinghotel.dtos.BookingDTO;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.responses.booking.BookingResponse;
import com.chinhbean.bookinghotel.responses.ResponseObject;
import com.chinhbean.bookinghotel.services.booking.IBookingService;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {
    private final IBookingService bookingService;

    @GetMapping("/get-booking-detail/{bookingId}")
    public ResponseEntity<ResponseObject> getBookingDetail(@PathVariable Long bookingId) {
        try {
            BookingResponse booking = bookingService.getBookingDetail(bookingId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(booking)
                    .message(MessageKeys.RETRIEVED_BOOKING_DETAIL_SUCCESSFULLY)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(MessageKeys.NO_BOOKINGS_FOUND)
                    .build());
        }
    }
    @PostMapping("/create-booking")
    public ResponseEntity<ResponseObject> createBooking(
            @Valid @RequestBody BookingDTO bookingDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(String.join(";", errorMessages))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        BookingResponse bookingResponse = bookingService.createBooking(bookingDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(bookingResponse)
                .message(MessageKeys.CREATE_BOOKING_SUCCESSFULLY)
                .build());
    }


}
