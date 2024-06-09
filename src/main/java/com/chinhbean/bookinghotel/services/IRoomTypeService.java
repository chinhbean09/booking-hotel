package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.RoomTypeDTO;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.responses.RoomTypeResponse;

import java.util.List;

public interface IRoomTypeService {

    RoomTypeResponse createRoomType(RoomTypeDTO roomTypeDTO) throws DataNotFoundException;

    List<RoomTypeResponse> getAllRoomTypesByHotelId(Long hotelId) throws DataNotFoundException;

    RoomTypeResponse updateRoomType(Long roomTypeId, RoomTypeDTO roomTypeDTO) throws DataNotFoundException;

    void deleteRoomType(Long id) throws DataNotFoundException;

    RoomTypeResponse getRoomTypeById(Long id) throws DataNotFoundException;
}
