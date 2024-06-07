//package com.chinhbean.bookinghotel.services;
//
//import com.chinhbean.bookinghotel.dtos.RoomDTO;
//import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
//import com.chinhbean.bookinghotel.responses.RoomResponse;
//
//import java.util.List;
//
//
//public interface IRoomService {
//
//
//    RoomResponse createRoom(RoomDTO roomDTO) throws DataNotFoundException;
//
//
//    List<RoomResponse> getAllRoomsByHotelId(Long hotelId) throws DataNotFoundException;
//
//    RoomResponse updateRoom(Long roomId, RoomDTO roomDTO) throws DataNotFoundException;
//
//
//    void deleteRoom(Long roomId) throws DataNotFoundException;
//}