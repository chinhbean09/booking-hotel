package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IHotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {
    @Query("SELECT h FROM Hotel h WHERE h.partner.id = :partnerId")
    Page<Hotel> findHotelsByPartnerId(@Param("partnerId") Long partnerId, Pageable pageable);

    Page<Hotel> findAllByStatus(HotelStatus hotelStatus, Pageable pageable);
}
