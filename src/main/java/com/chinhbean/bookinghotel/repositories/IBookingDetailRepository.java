package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.BookingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IBookingDetailRepository extends JpaRepository<BookingDetails, Long> {

    @Query("SELECT bd FROM BookingDetails bd WHERE bd.booking.bookingId = :bookingId")
    List<BookingDetails> findByBookingId(@Param("bookingId") Long bookingId);

}
