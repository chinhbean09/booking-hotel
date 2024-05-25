package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.RoomDTO;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.responses.RoomResponse;

import java.util.List;


public interface IRoomService {

    /**
     * Creates a new room based on the provided room details.
     *
     * @param roomDTO The details of the room to be created.
     * @return The response containing the created room details.
     */
    RoomResponse createRoom(RoomDTO roomDTO);

    /**
     * Retrieves a list of RoomResponse objects filtered by the given hotel ID.
     *
     * @param  hotelId  the ID of the hotel to filter the rooms by
     * @return          a list of RoomResponse objects representing the rooms of the hotel
     * @throws DataNotFoundException if no rooms are found for the given hotel ID
     */
    List<RoomResponse> getAllRoomsByHotelId(Long hotelId) throws DataNotFoundException;

    /**
     * Updates a room with the given room ID using the provided room details.
     *
     * @param  roomId   the ID of the room to be updated
     * @param  roomDTO  the updated room details
     * @return          the response containing the updated room details
     * @throws DataNotFoundException if the room with the given ID does not exist
     */
    RoomResponse updateRoom(Long roomId, RoomDTO roomDTO) throws DataNotFoundException;

    /**
     * Deletes a room with the specified ID.
     *
     * @param  roomId  the ID of the room to be deleted
     * @throws DataNotFoundException if the room with the specified ID does not exist
     */
    void deleteRoom(Long roomId) throws DataNotFoundException;
}