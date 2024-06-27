package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.RoomConvenience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IConvenienceRoomRepository extends JpaRepository<RoomConvenience, Long> {
    Optional<RoomConvenience> findByWardrobeAndAirConditioningAndTvAndWifiAndToiletriesAndKitchen(Boolean wardrobe, Boolean airConditioning, Boolean tv, Boolean wifi, Boolean toiletries, Boolean kitchen);
}
