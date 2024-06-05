package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.ConvenienceRoomDTO;
import com.chinhbean.bookinghotel.dtos.RoomDTO;
import com.chinhbean.bookinghotel.dtos.TypeRoomDTO;
import com.chinhbean.bookinghotel.entities.*;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.repositories.ConvenienceRoomRepository;
import com.chinhbean.bookinghotel.repositories.RoomImageRepository;
import com.chinhbean.bookinghotel.repositories.RoomRepository;
import com.chinhbean.bookinghotel.repositories.TypeRepository;
import com.chinhbean.bookinghotel.responses.RoomResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final TypeRepository typeRepository;
    private final ConvenienceRoomRepository convenienceRoomRepository;

    @Override
    @Transactional
    public RoomResponse createRoom(RoomDTO roomDTO) throws DataNotFoundException {

        // Check if the room number already exists
        if (roomRepository.existsByRoomNumberAndHotelId(roomDTO.getRoomNumber(), roomDTO.getHotelId())) {
            throw new DataNotFoundException(MessageKeys.ROOM_NUMBER_ALREADY_EXISTS);
        }

        // Convert the roomDTO to an entity
        Room room = convertToEntity(roomDTO);

        // Filter out the new types from the room's types
        Set<Type> newTypes = room.getTypes().stream()
                .filter(type -> type.getId() == null)
                .collect(Collectors.toSet());

        // Save the new types to the repository
        typeRepository.saveAll(newTypes);
        Set<RoomConvenience> newConveniences = room.getRoomConveniences().stream()
                .filter(convenience -> convenience.getId() == null)
                .collect(Collectors.toSet());
        convenienceRoomRepository.saveAll(newConveniences);

        // Save the room to the repository and get the saved room
        Room savedRoom = roomRepository.save(room);

        // Create a RoomResponse from the saved room
        return RoomResponse.fromRoom(savedRoom);
    }

    @Override
    public List<RoomResponse> getAllRoomsByHotelId(Long hotelId) throws DataNotFoundException {

        // Retrieve all rooms associated with the provided hotelId, including their types.
        List<Room> rooms = roomRepository.findByHotelIdWithTypesAndConvenienceAndRoomImages(hotelId);

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
    @Transactional
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
            for (TypeRoomDTO typeRoomDTO : roomDTO.getTypes()) {
                // Convert TypeDTO to Type entity
                Type type = convertToTypeEntity(typeRoomDTO);

                // Save the new type to the database
                type = typeRepository.save(type);

                // Add the saved type to the updated types set
                updatedTypes.add(type);
            }
            // Update the room's types
            room.setTypes(updatedTypes);
        }

        if (roomDTO.getConveniences() != null) {
            Set<RoomConvenience> updatedRoomConveniences = new HashSet<>();
            for (ConvenienceRoomDTO convenienceRoomDTO : roomDTO.getConveniences()) {
                RoomConvenience roomConvenience = convertToConvenienceRoomEntity(convenienceRoomDTO);
                roomConvenience = convenienceRoomRepository.save(roomConvenience);
                updatedRoomConveniences.add(roomConvenience);
            }
            room.setRoomConveniences(updatedRoomConveniences);
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

        // Delete associated room images
        room.getRoomImages().forEach(roomImageRepository::delete);

        // Delete the room
        roomRepository.delete(room);
    }


    private Room convertToEntity(RoomDTO roomDTO) {
        // Convert the list of TypeDTO objects to a set of Type objects
        Set<Type> types = roomDTO.getTypes().stream()
                .map(this::convertToTypeEntity)
                .collect(Collectors.toSet());
        Set<RoomConvenience> roomConveniences = roomDTO.getConveniences().stream()
                .map(this::convertToConvenienceRoomEntity)
                .collect(Collectors.toSet());
        // Initialize roomImages as an empty set to avoid null value
        Set<RoomImage> roomImages = Collections.emptySet();

        // Create a new Hotel object with the provided hotelId
        Hotel hotel = new Hotel();
        hotel.setId(roomDTO.getHotelId());

        // Build and return a new Room entity with the converted data
        return Room.builder()
                .hotel(hotel)
                .roomNumber(roomDTO.getRoomNumber())
                .price(roomDTO.getPrice())
                .availability(roomDTO.getAvailability())
                .roomImages(roomImages)
                .types(types)
                .roomConveniences(roomConveniences)
                .build();
    }



    private Type convertToTypeEntity(TypeRoomDTO typeRoomDTO) {
        // Find the Type entity in the repository based on the provided TypeDTO properties
        return typeRepository.findByLuxuryAndSingleBedroomAndTwinBedroomAndDoubleBedroom(
                        typeRoomDTO.getLuxury(),
                        typeRoomDTO.getSingleBedroom(),
                        typeRoomDTO.getTwinBedroom(),
                        typeRoomDTO.getDoubleBedroom())
                // If the Type entity does not exist, create a new Type entity using the provided TypeDTO properties
                .orElseGet(() -> createNewType(typeRoomDTO));
    }

    private RoomConvenience convertToConvenienceRoomEntity(ConvenienceRoomDTO dto) {
        return convenienceRoomRepository.findByWardrobeAndAirConditioningAndTvAndWifiAndToiletriesAndKitchen(
                        dto.getWardrobe(),
                        dto.getAirConditioning(),
                        dto.getTv(),
                        dto.getWifi(),
                        dto.getToiletries(),
                        dto.getKitchen())
                .orElseGet(() -> createNewConvenienceRoom(dto));
    }

    private RoomConvenience createNewConvenienceRoom(ConvenienceRoomDTO dto) {
        RoomConvenience roomConvenience = new RoomConvenience();
        roomConvenience.setWardrobe(dto.getWardrobe());
        roomConvenience.setAirConditioning(dto.getAirConditioning());
        roomConvenience.setTv(dto.getTv());
        roomConvenience.setWifi(dto.getWifi());
        roomConvenience.setToiletries(dto.getToiletries());
        roomConvenience.setKitchen(dto.getKitchen());
        // Set the association with Room
        Set<Room> rooms = new HashSet<>();
        roomConvenience.setRooms(rooms);
        return roomConvenience;
    }


    private Type createNewType(TypeRoomDTO typeRoomDTO) {
        Type type = new Type();
        type.setLuxury(typeRoomDTO.getLuxury());
        type.setSingleBedroom(typeRoomDTO.getSingleBedroom());
        type.setTwinBedroom(typeRoomDTO.getTwinBedroom());
        type.setDoubleBedroom(typeRoomDTO.getDoubleBedroom());
        return type;
    }
}