package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.RoomDTO;
import com.chinhbean.bookinghotel.dtos.TypeDTO;
import com.chinhbean.bookinghotel.entities.*;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.repositories.RoomRepository;
import com.chinhbean.bookinghotel.repositories.TypeRepository;
import com.chinhbean.bookinghotel.responses.RoomResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {
    private final RoomRepository roomRepository;
    private final TypeRepository typeRepository;
    /**
     * Creates a new room based on the provided RoomDTO.
     *
     * @param roomDTO The RoomDTO containing the room details.
     * @return The RoomResponse representing the created room.
     */
    @Override
    public RoomResponse createRoom(RoomDTO roomDTO) {
        // Convert the roomDTO to an entity
        Room room = convertToEntity(roomDTO);

        // Filter out the new types from the room's types
        Set<Type> newTypes = room.getTypes().stream()
                .filter(type -> type.getId() == null)
                .collect(Collectors.toSet());

        // Save the new types to the repository
        typeRepository.saveAll(newTypes);

        // Save the room to the repository and get the saved room
        Room savedRoom = roomRepository.save(room);

        // Create a RoomResponse from the saved room
        return RoomResponse.fromRoom(savedRoom);
    }

    /**
     * This method retrieves all rooms associated with a specific hotel.
     * It throws a DataNotFoundException if no rooms are found for the provided hotelId.
     *
     * @param hotelId The ID of the hotel for which to retrieve the rooms.
     * @return A list of RoomResponse objects representing the rooms.
     * @throws DataNotFoundException If no rooms are found for the provided hotelId.
     */
    @Override
    public List<RoomResponse> getAllRoomsByHotelId(Long hotelId) throws DataNotFoundException {

        // Retrieve all rooms associated with the provided hotelId, including their types.
        List<Room> rooms = roomRepository.findByHotelIdWithTypes(hotelId);

        // If no rooms are found, throw a DataNotFoundException.
        if (rooms.isEmpty()) {
            throw new DataNotFoundException(MessageKeys.NO_ROOMS_FOUND);
        }

        // Map each room to a RoomResponse object and collect the results into a list.
        return rooms.stream()
                .map(RoomResponse::fromRoom)
                .collect(Collectors.toList());
    }

    /**
     * This method updates a room in the database based on the provided RoomDTO.
     *
     * @param roomId The ID of the room to update.
     * @param roomDTO The RoomDTO containing the updated room details.
     * @return The RoomResponse representing the updated room.
     * @throws DataNotFoundException If the room with the provided ID does not exist.
     */
    @Override
    public RoomResponse updateRoom(Long roomId, RoomDTO roomDTO) throws DataNotFoundException {
        // Find the room with the provided ID, or throw an exception if it does not exist
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXISTS));

        // Update the room's details if they are provided in the RoomDTO
        if (roomDTO.getRoomNumber() != null) {
            // Update the room number
            room.setRoomNumber(roomDTO.getRoomNumber());
        }
        if (roomDTO.getPrice() != null) {
            // Update the room price
            room.setPrice(roomDTO.getPrice());
        }
        if (roomDTO.getAvailability() != null) {
            // Update the room availability
            room.setAvailability(roomDTO.getAvailability());
        }

        // If new types are provided in the RoomDTO, save them and associate them with the room
        if (roomDTO.getTypes() != null) {
            Set<Type> updatedTypes = new HashSet<>();
            for (TypeDTO typeDTO : roomDTO.getTypes()) {
                // Convert TypeDTO to Type entity
                Type type = convertToTypeEntity(typeDTO);

                // Save the new type to the database
                type = typeRepository.save(type);

                // Add the saved type to the updated types set
                updatedTypes.add(type);
            }
            // Update the room's types
            room.setTypes(updatedTypes);
        }

        // Save the updated room to the database and get the saved room
        Room savedRoom = roomRepository.save(room);

        // Create a RoomResponse from the saved room and return it
        return RoomResponse.fromRoom(savedRoom);
    }



    /**
     * Deletes a room from the database, including its associated types.
     *
     * @param roomId The ID of the room to delete.
     * @throws DataNotFoundException If the room with the provided ID does not exist.
     */
    @Override
    @Transactional
    public void deleteRoom(Long roomId) throws DataNotFoundException {
        // Find the room with the provided ID, or throw an exception if it does not exist
        Room room = roomRepository.findWithTypesById(roomId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXISTS));

        // Delete each associated type of the room
        room.getTypes().forEach(typeRepository::delete);

        // Delete the room
        roomRepository.delete(room);
    }


    /**
     * Converts a RoomDTO object to a Room entity.
     *
     * @param roomDTO The RoomDTO object to convert.
     * @return The converted Room entity.
     */
    private Room convertToEntity(RoomDTO roomDTO) {
        // Convert the list of TypeDTO objects to a set of Type objects
        Set<Type> types = roomDTO.getTypes().stream()
                .map(this::convertToTypeEntity)
                .collect(Collectors.toSet());

        // Create a new Hotel object with the provided hotelId
        Hotel hotel = new Hotel();
        hotel.setId(roomDTO.getHotelId());

        // Build and return a new Room entity with the converted data
        return Room.builder()
                .hotel(hotel)
                .roomNumber(roomDTO.getRoomNumber())
                .price(roomDTO.getPrice())
                .availability(roomDTO.getAvailability())
                .types(types)
                .build();
    }


    /**
     * Converts a TypeDTO object to a Type entity.
     * If the Type entity does not exist in the repository, creates a new Type entity.
     *
     * @param typeDTO The TypeDTO object to convert.
     * @return The converted Type entity.
     */
    private Type convertToTypeEntity(TypeDTO typeDTO) {
        // Find the Type entity in the repository based on the provided TypeDTO properties
        return typeRepository.findByLuxuryAndSingleBedroomAndTwinBedroomAndDoubleBedroom(
                        typeDTO.getLuxury(),
                        typeDTO.getSingleBedroom(),
                        typeDTO.getTwinBedroom(),
                        typeDTO.getDoubleBedroom())
                // If the Type entity does not exist, create a new Type entity using the provided TypeDTO properties
                .orElseGet(() -> createNewType(typeDTO));
    }

    /**
     * Creates a new Type entity based on the provided TypeDTO.
     *
     * @param typeDTO The TypeDTO object used to create the new Type entity.
     * @return The newly created Type entity.
     */
    private Type createNewType(TypeDTO typeDTO) {
        Type type = new Type();
        type.setLuxury(typeDTO.getLuxury());
        type.setSingleBedroom(typeDTO.getSingleBedroom());
        type.setTwinBedroom(typeDTO.getTwinBedroom());
        type.setDoubleBedroom(typeDTO.getDoubleBedroom());
        return type;
    }
}