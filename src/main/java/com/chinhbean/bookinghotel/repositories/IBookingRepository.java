package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Booking;
import com.chinhbean.bookinghotel.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Set;

public interface IBookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b JOIN b.bookingDetails bd JOIN bd.roomType rt JOIN rt.hotel h WHERE h.partner.id = :partnerId")
    Page<Booking> findAllByPartnerId(@Param("partnerId") Long partnerId, Pageable pageable);

    Page<Booking> findAllByUserId(Long userId, Pageable pageable);

    Set<Booking> findAllByStatusAndExpirationDateBefore(BookingStatus status, LocalDateTime dateTime);

}
