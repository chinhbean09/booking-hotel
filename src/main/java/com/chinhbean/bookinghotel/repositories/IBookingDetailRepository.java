package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.BookingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookingDetailRepository extends JpaRepository<BookingDetails, Long> {
}
