package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.RoomTypeDTO;
import com.chinhbean.bookinghotel.enums.RoomTypeStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.responses.RoomTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IRoomTypeService {

    RoomTypeResponse createRoomType(RoomTypeDTO roomTypeDTO) throws DataNotFoundException, PermissionDenyException;

    Page<RoomTypeResponse> getAllRoomTypesByHotelId(Long hotelId, int page, int size) throws DataNotFoundException;

    RoomTypeResponse updateRoomType(Long roomTypeId, RoomTypeDTO roomTypeDTO) throws DataNotFoundException;

    void deleteRoomType(Long id) throws DataNotFoundException;

    RoomTypeResponse getRoomTypeById(Long id) throws DataNotFoundException;

    List<RoomTypeResponse> filterRoomType(Long hotelId, Boolean luxury, Boolean singleBedroom, Boolean twinBedroom,
                                          Boolean doubleBedroom, Boolean wardrobe, Boolean airConditioning,
                                          Boolean tv, Boolean wifi, Boolean toiletries, Boolean kitchen,
                                          Double minPrice, Double maxPrice);

    void updateStatus(Long roomTypeId, RoomTypeStatus newStatus) throws DataNotFoundException, PermissionDenyException;

    Page<RoomTypeResponse> getAllRoomTypesByStatus(Long hotelId, int page, int size) throws DataNotFoundException;

    Page<RoomTypeResponse> getAvailableRoomTypesByHotelIdAndDates(Long hotelId, LocalDate checkIn, LocalDate checkOut, Pageable pageable) throws DataNotFoundException;


}
