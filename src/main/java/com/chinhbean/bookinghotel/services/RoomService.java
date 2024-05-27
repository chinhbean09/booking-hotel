package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.ConvenienceRoomDTO;
import com.chinhbean.bookinghotel.dtos.RoomDTO;
import com.chinhbean.bookinghotel.dtos.TypeRoomDTO;
import com.chinhbean.bookinghotel.entities.*;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.repositories.ConvenienceRoomRepository;
import com.chinhbean.bookinghotel.repositories.RoomRepository;
import com.chinhbean.bookinghotel.repositories.TypeRoomRepository;
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
    private final TypeRoomRepository typeRoomRepository;
    private final ConvenienceRoomRepository convenienceRoomRepository;

    @Override
    public RoomResponse createRoom(RoomDTO roomDTO) throws DataNotFoundException {

        // Check if the room number already exists
        if (roomRepository.existsByRoomNumber(roomDTO.getRoomNumber())) {
            throw new DataNotFoundException(MessageKeys.ROOM_NUMBER_ALREADY_EXISTS);
        }

        // Convert the roomDTO to an entity
        Room room = convertToEntity(roomDTO);

        // Filter out the new types from the room's types
        Set<TypeRoom> newTypeRooms = room.getTypeRooms().stream()
                .filter(type -> type.getId() == null)
                .collect(Collectors.toSet());

        // Save the new types to the repository
        typeRoomRepository.saveAll(newTypeRooms);

        // Save the room to the repository and get the saved room
        Room savedRoom = roomRepository.save(room);

        // Create a RoomResponse from the saved room
        return RoomResponse.fromRoom(savedRoom);
    }

    @Override
    public List<RoomResponse> getAllRoomsByHotelId(Long hotelId) throws DataNotFoundException {

        // Retrieve all rooms associated with the provided hotelId, including their types.
        List<Room> rooms = roomRepository.findByHotelIdWithTypesAndConvenience(hotelId);

        // If no rooms are found, throw a DataNotFoundException.
        if (rooms.isEmpty()) {
            throw new DataNotFoundException(MessageKeys.NO_ROOMS_FOUND);
        }

        // Map each room to a RoomResponse object and collect the results into a list.
        return rooms.stream()
                .map(RoomResponse::fromRoom)
                .collect(Collectors.toList());
    }

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
            Set<TypeRoom> updatedTypeRooms = new HashSet<>();
            for (TypeRoomDTO typeRoomDTO : roomDTO.getTypes()) {
                // Convert TypeDTO to Type entity
                TypeRoom typeRoom = convertToTypeEntity(typeRoomDTO);

                // Save the new type to the database
                typeRoom = typeRoomRepository.save(typeRoom);

                // Add the saved type to the updated types set
                updatedTypeRooms.add(typeRoom);
            }
            // Update the room's types
            room.setTypeRooms(updatedTypeRooms);
        }

        if (roomDTO.getConveniences() != null) {
            Set<ConvenienceRoom> updatedConvenienceRooms = new HashSet<>();
            for (ConvenienceRoomDTO convenienceRoomDTO : roomDTO.getConveniences()) {
                ConvenienceRoom convenienceRoom = convertToConvenienceRoomEntity(convenienceRoomDTO);
                convenienceRoom = convenienceRoomRepository.save(convenienceRoom);
                updatedConvenienceRooms.add(convenienceRoom);
            }
            room.setConvenienceRooms(updatedConvenienceRooms);
        }


        // Save the updated room to the database and get the saved room
        Room savedRoom = roomRepository.save(room);

        // Create a RoomResponse from the saved room and return it
        return RoomResponse.fromRoom(savedRoom);
    }



    @Override
    @Transactional
    public void deleteRoom(Long roomId) throws DataNotFoundException {
        // Find the room with the provided ID, or throw an exception if it does not exist
        Room room = roomRepository.findWithTypesAndConvenienceById(roomId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXISTS));

        // Delete each associated type of the room
        //room.getTypeRooms().forEach(typeRepository::delete);

        // Delete the room
        roomRepository.delete(room);
    }


    private Room convertToEntity(RoomDTO roomDTO) {
        // Convert the list of TypeDTO objects to a set of Type objects
        Set<TypeRoom> typeRooms = roomDTO.getTypes().stream()
                .map(this::convertToTypeEntity)
                .collect(Collectors.toSet());
        Set<ConvenienceRoom> convenienceRooms = roomDTO.getConveniences().stream()
                .map(this::convertToConvenienceRoomEntity)
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
                .typeRooms(typeRooms)
                .convenienceRooms(convenienceRooms)
                .build();
    }



    private TypeRoom convertToTypeEntity(TypeRoomDTO typeRoomDTO) {
        // Find the Type entity in the repository based on the provided TypeDTO properties
        return typeRoomRepository.findByLuxuryAndSingleBedroomAndTwinBedroomAndDoubleBedroom(
                        typeRoomDTO.getLuxury(),
                        typeRoomDTO.getSingleBedroom(),
                        typeRoomDTO.getTwinBedroom(),
                        typeRoomDTO.getDoubleBedroom())
                // If the Type entity does not exist, create a new Type entity using the provided TypeDTO properties
                .orElseGet(() -> createNewType(typeRoomDTO));
    }

    private ConvenienceRoom convertToConvenienceRoomEntity(ConvenienceRoomDTO dto) {
        return convenienceRoomRepository.findByWardrobeAndAirConditioningAndTvAndWifiAndToiletriesAndKitchen(
                        dto.getWardrobe(),
                        dto.getAirConditioning(),
                        dto.getTv(),
                        dto.getWifi(),
                        dto.getToiletries(),
                        dto.getKitchen())
                .orElseGet(() -> createNewConvenienceRoom(dto));
    }

    private ConvenienceRoom createNewConvenienceRoom(ConvenienceRoomDTO dto) {
        ConvenienceRoom convenienceRoom = new ConvenienceRoom();
        convenienceRoom.setWardrobe(dto.getWardrobe());
        convenienceRoom.setAirConditioning(dto.getAirConditioning());
        convenienceRoom.setTv(dto.getTv());
        convenienceRoom.setWifi(dto.getWifi());
        convenienceRoom.setToiletries(dto.getToiletries());
        convenienceRoom.setKitchen(dto.getKitchen());
        return convenienceRoom;
    }


    private TypeRoom createNewType(TypeRoomDTO typeRoomDTO) {
        TypeRoom typeRoom = new TypeRoom();
        typeRoom.setLuxury(typeRoomDTO.getLuxury());
        typeRoom.setSingleBedroom(typeRoomDTO.getSingleBedroom());
        typeRoom.setTwinBedroom(typeRoomDTO.getTwinBedroom());
        typeRoom.setDoubleBedroom(typeRoomDTO.getDoubleBedroom());
        return typeRoom;
    }
}