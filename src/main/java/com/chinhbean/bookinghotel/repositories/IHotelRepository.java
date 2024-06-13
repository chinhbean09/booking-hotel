package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Set;

@Repository
public interface IHotelRepository extends JpaRepository<Hotel, Long> {
    Page<Hotel> findAllByPartnerId(Long userId, Pageable pageable);

    @Query("SELECT h FROM Hotel h JOIN h.roomTypes rt WHERE h.location.province = :province AND rt.capacityPerRoom = :numPeople AND h NOT IN (SELECT bd.roomType.hotel FROM Booking b JOIN b.bookingDetails bd WHERE (b.checkInDate BETWEEN :checkInDate AND :checkOutDate) OR (b.checkOutDate BETWEEN :checkInDate AND :checkOutDate))")
    Page<Hotel> findByProvinceAndCapacityPerRoomAndAvailability(String province, int numPeople, Date checkInDate, Date checkOutDate, Pageable pageable);

    @Query("SELECT h FROM Hotel h JOIN h.roomTypes rt JOIN rt.type t JOIN h.conveniences c WHERE t.id = :typeId AND t.luxury = :luxury AND t.singleBedroom = :singleBedroom AND t.twinBedroom = :twinBedroom AND t.doubleBedroom = :doubleBedroom AND h.location.province = :province AND h.rating = :rating AND c.id IN :convenienceIds AND c.freeBreakfast = :freeBreakfast AND c.pickUpDropOff = :pickUpDropOff AND c.restaurant = :restaurant AND c.bar = :bar AND c.pool = :pool AND c.freeInternet = :freeInternet AND c.reception24h = :reception24h AND c.laundry = :laundry")
    Page<Hotel> filterHotels(@Param("province") String province, @Param("rating") Integer rating, @Param("convenienceIds") Set<Long> convenienceIds, @Param("typeId") Long typeId, @Param("luxury") Boolean luxury, @Param("singleBedroom") Boolean singleBedroom, @Param("twinBedroom") Boolean twinBedroom, @Param("doubleBedroom") Boolean doubleBedroom, @Param("freeBreakfast") Boolean freeBreakfast, @Param("pickUpDropOff") Boolean pickUpDropOff, @Param("restaurant") Boolean restaurant, @Param("bar") Boolean bar, @Param("pool") Boolean pool, @Param("freeInternet") Boolean freeInternet, @Param("reception24h") Boolean reception24h, @Param("laundry") Boolean laundry, Pageable pageable);
}
