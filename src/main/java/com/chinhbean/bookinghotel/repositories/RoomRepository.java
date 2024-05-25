package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT DISTINCT r FROM Room r LEFT JOIN FETCH r.types WHERE r.hotel.id = :hotelId")
    List<Room> findByHotelIdWithTypes(Long hotelId);

    @EntityGraph(attributePaths = "types")
    Optional<Room> findWithTypesById(Long id);
}