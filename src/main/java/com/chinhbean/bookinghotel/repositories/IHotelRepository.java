package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Convenience;
import com.chinhbean.bookinghotel.entities.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Set;

@Repository
public interface IHotelRepository extends JpaRepository<Hotel, Long> {
    Page<Hotel> findAllByPartnerId(Long userId, Pageable pageable);

    @Query("SELECT h FROM Hotel h JOIN h.roomTypes rt WHERE h.location.province = :province AND rt.capacityPerRoom = :numPeople AND h NOT IN (SELECT bd.roomType.hotel FROM Booking b JOIN b.bookingDetails bd WHERE (b.checkInDate BETWEEN :checkInDate AND :checkOutDate) OR (b.checkOutDate BETWEEN :checkInDate AND :checkOutDate))")
    Page<Hotel> findByProvinceAndCapacityPerRoomAndAvailability(String province, int numPeople, Date checkInDate, Date checkOutDate, Pageable pageable);

    Page<Hotel> filterHotels(String location, Set<Convenience> conveniences, Double rating, Pageable pageable);
}
