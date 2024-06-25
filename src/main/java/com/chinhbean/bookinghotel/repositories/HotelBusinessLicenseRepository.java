package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.HotelBusinessLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelBusinessLicenseRepository extends JpaRepository<HotelBusinessLicense, Long> {

    Optional<HotelBusinessLicense> findByBusinessLicenseAndHotelId(String businessLicense, Long hotelId);
}
