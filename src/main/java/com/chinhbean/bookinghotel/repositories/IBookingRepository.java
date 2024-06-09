package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookingRepository extends JpaRepository<Booking, Long> {
}
