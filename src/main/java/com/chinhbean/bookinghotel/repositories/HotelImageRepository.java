package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.HotelImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelImageRepository extends JpaRepository<HotelImages, Long> {

    Optional<HotelImages> findByImageUrlAndHotelId(String imageUrl, Long hotelId);
}
