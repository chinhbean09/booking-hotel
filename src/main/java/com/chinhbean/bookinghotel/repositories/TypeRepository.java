package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {

    Optional<Type> findByLuxuryAndSingleBedroomAndTwinBedroomAndDoubleBedroomAndWardrobeAndAirConditioningAndTvAndWifiAndToiletriesAndKitchen
            (Boolean luxury,
             Boolean singleBedroom,
             Boolean twinBedroom,
             Boolean doubleBedroom,
             Boolean wardrobe,
             Boolean airConditioning,
             Boolean tv,
             Boolean wifi,
             Boolean toiletries,
             Boolean kitchen);
}