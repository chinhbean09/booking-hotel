package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IHotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {
    Page<Hotel> findAllByPartnerId(Long userId, Pageable pageable);

    Page<Hotel> findAllByStatus(HotelStatus hotelStatus, Pageable pageable);
}
