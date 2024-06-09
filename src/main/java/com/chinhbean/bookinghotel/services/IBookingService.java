package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.BookingDTO;
import com.chinhbean.bookinghotel.entities.Booking;
import com.chinhbean.bookinghotel.enums.BookingStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.responses.BookingResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

public interface IBookingService {

    @Transactional
    Page<BookingResponse> getListBooking() throws DataNotFoundException;

    @Transactional
    Booking createBooking(BookingDTO bookingDTO, String token);

    @Transactional
    BookingResponse getBookingDetail(Long bookingId) throws DataNotFoundException;

    @Transactional
    Booking updateBooking(Long bookingId, BookingDTO bookingDTO) throws DataNotFoundException;

    @Transactional
    void updateStatus(Long bookingId, BookingStatus newStatus, String token) throws DataNotFoundException, PermissionDenyException, PermissionDenyException;
}
