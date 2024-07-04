package com.chinhbean.bookinghotel.controllers;

import com.chinhbean.bookinghotel.dtos.BookingDTO;
import com.chinhbean.bookinghotel.entities.Booking;
import com.chinhbean.bookinghotel.enums.BookingStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.responses.ResponseObject;
import com.chinhbean.bookinghotel.responses.booking.BookingResponse;
import com.chinhbean.bookinghotel.services.booking.IBookingService;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER','ROLE_PARTNER')")
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
            BindingResult result) {
        try {
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
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(bookingResponse)
                            .message(MessageKeys.CREATE_BOOKING_SUCCESSFULLY)
                            .build());
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Xử lý các loại exception ném ra từ BookingService
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .message(e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        } catch (Exception e) {
            // Xử lý các exception khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .message("Failed to create booking.")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }

    @GetMapping("/get-bookings")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER','ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> getListBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<BookingResponse> bookings = bookingService.getListBooking(page, size);
            if (bookings.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message(MessageKeys.NO_BOOKINGS_FOUND)
                        .build());
            } else {
                return ResponseEntity.ok().body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(bookings)
                        .message(MessageKeys.RETRIEVED_ALL_BOOKINGS_SUCCESSFULLY)
                        .build());
            }
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(MessageKeys.NO_BOOKINGS_FOUND)
                    .build());
        } catch (PermissionDenyException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseObject.builder()
                    .status(HttpStatus.FORBIDDEN)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/update-booking/{bookingId}")
    @PreAuthorize("hasAnyAuthority('ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> updateBooking(@PathVariable Long bookingId, @RequestBody BookingDTO bookingDTO) {
        try {
            Booking updatedBooking = bookingService.updateBooking(bookingId, bookingDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(updatedBooking)
                    .message(MessageKeys.UPDATE_BOOKING_SUCCESSFULLY)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(MessageKeys.NO_BOOKINGS_FOUND)
                    .build());
        }
    }

    @PutMapping("/update-status/{bookingId}")
    @PreAuthorize("hasAnyAuthority('ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> updateStatus(@PathVariable Long bookingId, @RequestBody BookingStatus newStatus) {
        try {
            bookingService.updateStatus(bookingId, newStatus);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(MessageKeys.UPDATE_BOOKING_STATUS_SUCCESSFULLY)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(MessageKeys.NO_BOOKINGS_FOUND)
                    .build());
        } catch (PermissionDenyException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseObject.builder()
                    .status(HttpStatus.FORBIDDEN)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/get-bookings-by-hotel/{hotelId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> getBookingsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<BookingResponse> bookings = bookingService.getBookingsByHotel(hotelId, page, size);
            if (bookings.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message(MessageKeys.NO_BOOKINGS_FOUND)
                        .build());
            } else {
                return ResponseEntity.ok().body(ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(bookings)
                        .message(MessageKeys.RETRIEVED_ALL_BOOKINGS_SUCCESSFULLY)
                        .build());
            }
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(MessageKeys.NO_BOOKINGS_FOUND)
                    .build());
        } catch (PermissionDenyException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseObject.builder()
                    .status(HttpStatus.FORBIDDEN)
                    .message(e.getMessage())
                    .build());
        }
    }


}
