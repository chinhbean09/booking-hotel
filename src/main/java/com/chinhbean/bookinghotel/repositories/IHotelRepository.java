package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface IHotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {
    @Query("SELECT h FROM Hotel h WHERE h.partner.id = :partnerId")
    Page<Hotel> findHotelsByPartnerId(@Param("partnerId") Long partnerId, Pageable pageable);

    Page<Hotel> findAllByStatus(HotelStatus hotelStatus, Pageable pageable);

    @Query("SELECT DISTINCT h FROM Hotel h " +
            "JOIN h.roomTypes rt " +
            "JOIN h.location hl " +
            "WHERE hl.province = :province " +
            "AND h.status = 'ACTIVE' " +
            "AND rt.capacityPerRoom >= :capacity " +
            "AND NOT EXISTS (" +
            "  SELECT bd FROM BookingDetails bd " +
            "  JOIN bd.booking b " +
            "  WHERE bd.roomType.id = rt.id " +
            "  AND b.checkOutDate > :checkIn " +
            "  AND b.checkInDate < :checkOut" +
            ")")
    Page<Hotel> findHotelsByProvinceAndDatesAndCapacity(
            @Param("province") String province,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("capacity") int capacity,
            Pageable pageable);
}
