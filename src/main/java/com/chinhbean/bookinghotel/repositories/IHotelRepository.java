package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IHotelRepository extends JpaRepository<Hotel, Long> {
    Page<Hotel> findAllByPartnerId(Long userId, Pageable pageable);
}
