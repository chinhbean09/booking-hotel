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


    @Query("SELECT h FROM Hotel h " +
            "LEFT JOIN FETCH h.conveniences c " +
            "LEFT JOIN FETCH h.roomTypes rt " +
            "LEFT JOIN FETCH rt.type t " +
            "LEFT JOIN FETCH rt.roomConveniences rc " +
            "LEFT JOIN FETCH rt.roomImages " +
            "LEFT JOIN FETCH h.location l " +
            "WHERE (h.rating = :rating OR :rating IS NULL) AND " +
            "(c.freeBreakfast = :freeBreakfast OR :freeBreakfast IS NULL) AND " +
            "(c.pickUpDropOff = :pickUpDropOff OR :pickUpDropOff IS NULL) AND " +
            "(c.restaurant = :restaurant OR :restaurant IS NULL) AND " +
            "(c.bar = :bar OR :bar IS NULL) AND " +
            "(c.pool = :pool OR :pool IS NULL) AND " +
            "(c.freeInternet = :freeInternet OR :freeInternet IS NULL) AND " +
            "(c.reception24h = :reception24h OR :reception24h IS NULL) AND " +
            "(c.laundry = :laundry OR :laundry IS NULL) AND " +
            "h.status = 'ACTIVE'")
    Page<Hotel> filterHotelWithConvenience(Integer rating,
                                           Boolean freeBreakfast,
                                           Boolean pickUpDropOff,
                                           Boolean restaurant,
                                           Boolean bar,
                                           Boolean pool,
                                           Boolean freeInternet,
                                           Boolean reception24h,
                                           Boolean laundry,
                                           Pageable pageable);

}
