package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.RoomType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Query("SELECT DISTINCT r FROM RoomType r LEFT JOIN FETCH r.type WHERE r.hotel.id = :hotelId")
    List<RoomType> findWithTypesByHotelId(Long hotelId);

    @Query("SELECT DISTINCT r FROM RoomType r LEFT JOIN FETCH r.type LEFT JOIN FETCH r.roomConveniences WHERE r.hotel.id = :hotelId")
    List<RoomType> findWithTypesAndRoomConveniencesByHotelId(Long hotelId);

    @EntityGraph(attributePaths = {"type", "roomConveniences", "roomImages"})
    //@Query("SELECT r FROM RoomType r WHERE r.id = :id")
    Optional<RoomType> findWithTypesAndRoomConveniencesById(Long id);
}
