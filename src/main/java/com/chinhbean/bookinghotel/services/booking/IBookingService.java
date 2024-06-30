package com.chinhbean.bookinghotel.services.booking;

import com.chinhbean.bookinghotel.dtos.BookingDTO;
import com.chinhbean.bookinghotel.entities.Booking;
import com.chinhbean.bookinghotel.enums.BookingStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.responses.booking.BookingResponse;
import org.springframework.data.domain.Page;

public interface IBookingService {

    BookingResponse createBooking(BookingDTO bookingDTO) throws Exception;

    BookingResponse getBookingDetail(Long bookingId) throws DataNotFoundException;

    Page<BookingResponse> getListBooking(String token, int page, int size) throws DataNotFoundException, PermissionDenyException;

     Booking updateBooking(Long bookingId, BookingDTO bookingDTO, String token) throws DataNotFoundException;

     void updateStatus(Long bookingId, BookingStatus newStatus, String token) throws DataNotFoundException, PermissionDenyException;

}
