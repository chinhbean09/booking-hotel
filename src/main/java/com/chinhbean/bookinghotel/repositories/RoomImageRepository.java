package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    Optional<RoomImage> findByImageUrlsAndRoomTypeId(String imageUrl, Long roomTypeId);
}
