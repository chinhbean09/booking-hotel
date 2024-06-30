package com.chinhbean.bookinghotel.services.booking;

import com.chinhbean.bookinghotel.dtos.BookingDTO;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.responses.booking.BookingResponse;

public interface IBookingService {

    BookingResponse createBooking(BookingDTO bookingDTO) throws Exception;

    BookingResponse getBookingDetail(Long bookingId) throws DataNotFoundException;

}
