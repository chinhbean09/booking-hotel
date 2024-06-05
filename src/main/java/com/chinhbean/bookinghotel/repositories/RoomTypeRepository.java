package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Query("SELECT DISTINCT r FROM RoomType r LEFT JOIN FETCH r.type WHERE r.hotel.id = :hotelId")
    List<RoomType> findWithTypesByHotelId(Long hotelId);
}
