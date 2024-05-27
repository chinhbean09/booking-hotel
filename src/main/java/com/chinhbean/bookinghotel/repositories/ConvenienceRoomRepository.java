package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.ConvenienceRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConvenienceRoomRepository extends JpaRepository<ConvenienceRoom, Long> {
    Optional<ConvenienceRoom> findByWardrobeAndAirConditioningAndTvAndWifiAndToiletriesAndKitchen(Boolean wardrobe, Boolean airConditioning, Boolean tv, Boolean wifi, Boolean toiletries, Boolean kitchen);
}
