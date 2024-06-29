package com.chinhbean.bookinghotel.repositories;

import com.chinhbean.bookinghotel.entities.RoomType;
import com.chinhbean.bookinghotel.enums.RoomTypeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IRoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Query("SELECT DISTINCT r FROM RoomType r LEFT JOIN FETCH r.type WHERE r.hotel.id = :hotelId")
    List<RoomType> findWithTypesByHotelId(Long hotelId);

    @Query("SELECT DISTINCT rt FROM RoomType rt " +
            "LEFT JOIN FETCH rt.type " +
            "LEFT JOIN FETCH rt.roomConveniences " +
            "WHERE rt.hotel.id = :hotelId " +
            "AND NOT EXISTS (" +
            "  SELECT bd FROM BookingDetails bd " +
            "  JOIN bd.booking b " +
            "  WHERE bd.roomType.id = rt.id " +
            "  AND b.checkOutDate > :checkIn " +
            "  AND b.checkInDate < :checkOut" +
            ")")
    Page<RoomType> findAvailableRoomsByHotelIdAndDates(@Param("hotelId") Long hotelId,
                                                       @Param("checkIn") LocalDate checkIn,
                                                       @Param("checkOut") LocalDate checkOut,
                                                       Pageable pageable);



    @EntityGraph(attributePaths = {"type", "roomConveniences", "roomImages"})
    Optional<RoomType> findWithTypesAndRoomConveniencesById(Long id);

    @Query("SELECT rt FROM RoomType rt JOIN rt.type t JOIN rt.roomConveniences rc WHERE " +
            "rt.hotel.id = :hotelId AND " +
            "(t.luxury = :luxury OR :luxury IS NULL) AND " +
            "(t.singleBedroom = :singleBedroom OR :singleBedroom IS NULL) AND " +
            "(t.twinBedroom = :twinBedroom OR :twinBedroom IS NULL) AND " +
            "(t.doubleBedroom = :doubleBedroom OR :doubleBedroom IS NULL) AND " +
            "(rc.wardrobe = :wardrobe OR :wardrobe IS NULL) AND " +
            "(rc.airConditioning = :airConditioning OR :airConditioning IS NULL) AND " +
            "(rc.tv = :tv OR :tv IS NULL) AND " +
            "(rc.wifi = :wifi OR :wifi IS NULL) AND " +
            "(rc.toiletries = :toiletries OR :toiletries IS NULL) AND " +
            "(rc.kitchen = :kitchen OR :kitchen IS NULL) AND " +
            "(rt.roomPrice >= :minPrice OR :minPrice IS NULL) AND " +
            "(rt.roomPrice <= :maxPrice OR :maxPrice IS NULL) AND " +
            "rt.status = "AVAILABLE")
    List<RoomType> findByTypeAndConveniencesAndPriceAndHotel(
            @Param("hotelId") Long hotelId,
            @Param("luxury") Boolean luxury,
            @Param("singleBedroom") Boolean singleBedroom,
            @Param("twinBedroom") Boolean twinBedroom,
            @Param("doubleBedroom") Boolean doubleBedroom,
            @Param("wardrobe") Boolean wardrobe,
            @Param("airConditioning") Boolean airConditioning,
            @Param("tv") Boolean tv,
            @Param("wifi") Boolean wifi,
            @Param("toiletries") Boolean toiletries,
            @Param("kitchen") Boolean kitchen,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

    Page<RoomType> findAllByStatusAndHotelId(RoomTypeStatus roomTypeStatus, Pageable pageable, Long hotelId);
}
