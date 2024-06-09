package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoomTypeRepository extends JpaRepository<RoomType, Long> {
}
