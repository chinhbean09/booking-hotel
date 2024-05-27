package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.TypeRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRoomRepository extends JpaRepository<TypeRoom, Long> {

    Optional<TypeRoom> findByLuxuryAndSingleBedroomAndTwinBedroomAndDoubleBedroom(Boolean luxury, Boolean singleBedroom, Boolean twinBedroom, Boolean doubleBedroom);
}