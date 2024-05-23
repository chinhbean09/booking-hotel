package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Convenience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConvenienceRepository extends JpaRepository<Convenience, Long> {
    Optional<Convenience> findByFreeBreakfastAndPickUpDropOffAndRestaurantAndBarAndPoolAndFreeInternetAndReception24hAndLaundry(
            Boolean freeBreakfast,
            Boolean pickUpDropOff,
            Boolean restaurant,
            Boolean bar,
            Boolean pool,
            Boolean freeInternet,
            Boolean reception24h,
            Boolean laundry);
}
