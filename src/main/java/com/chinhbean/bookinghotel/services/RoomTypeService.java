package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.ConvenienceRoomDTO;
import com.chinhbean.bookinghotel.dtos.RoomTypeDTO;
import com.chinhbean.bookinghotel.dtos.TypeRoomDTO;
import com.chinhbean.bookinghotel.entities.*;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.repositories.ConvenienceRoomRepository;
import com.chinhbean.bookinghotel.repositories.RoomImageRepository;
import com.chinhbean.bookinghotel.repositories.RoomTypeRepository;
import com.chinhbean.bookinghotel.repositories.TypeRepository;
import com.chinhbean.bookinghotel.responses.RoomTypeResponse;
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
public class RoomTypeService implements IRoomTypeService {

    private final TypeRepository typeRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ConvenienceRoomRepository convenienceRoomRepository;
    private final RoomImageRepository roomImageRepository;

    @Override
    @Transactional
    public RoomTypeResponse createRoomType(RoomTypeDTO roomTypeDTO) throws DataNotFoundException {
        // Convert DTO to entity
        RoomType roomType = convertToEntity(roomTypeDTO);

        // Save the new Type entity
        Type newType = convertToTypeEntity(roomTypeDTO.getTypes());
        Type savedType = typeRepository.save(newType);

        // Assign the saved Type to the RoomType
        roomType.setType(savedType);
        Set<RoomConvenience> newConveniences = roomType.getRoomConveniences().stream()
                .filter(convenience -> convenience.getId() == null)
                .collect(Collectors.toSet());
        convenienceRoomRepository.saveAll(newConveniences);
        //roomType.setRoomConveniences(newConveniences);

        // Save the RoomType
        RoomType savedRoomType = roomTypeRepository.save(roomType);

        // Return the RoomType response
        return RoomTypeResponse.fromType(savedRoomType);
    }


    @Override
    public List<RoomTypeResponse> getAllRoomTypesByHotelId(Long hotelId) throws DataNotFoundException {
        List<RoomType> roomTypes = roomTypeRepository.findWithTypesAndRoomConveniencesByHotelId(hotelId);

        if (roomTypes.isEmpty()) {
            throw new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND);
        }
        return roomTypes.stream()
                .map(RoomTypeResponse::fromType)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoomTypeResponse updateRoomType(Long roomTypeId, RoomTypeDTO roomTypeDTO) throws DataNotFoundException {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND));

        if (roomTypeDTO.getDescription() != null) {
            roomType.setDescription(roomTypeDTO.getDescription());
        }
        if (roomTypeDTO.getNumberOfRooms() != null) {
            roomType.setNumberOfRoom(roomTypeDTO.getNumberOfRooms());
        }
        if (roomTypeDTO.getRoomPrice() != null) {
            roomType.setRoomPrice(roomTypeDTO.getRoomPrice());
        }
        if (roomTypeDTO.getStatus() != null) {
            roomType.setStatus(roomTypeDTO.getStatus());
        }

        if (roomTypeDTO.getTypes() != null) {
            TypeRoomDTO typeRoomDTO = roomTypeDTO.getTypes(); // Assuming getTypes() returns a single TypeRoomDTO object
            Type type = convertToTypeEntity(typeRoomDTO);
            typeRepository.save(type);
            roomType.setType(type);
        }
        if (roomTypeDTO.getConveniences() != null) {
            Set<RoomConvenience> updatedRoomConveniences = new HashSet<>();
            for (ConvenienceRoomDTO convenienceRoomDTO : roomTypeDTO.getConveniences()) {
                RoomConvenience roomConvenience = convertToRoomConvenienceEntity(convenienceRoomDTO);
                roomConvenience = convenienceRoomRepository.save(roomConvenience);
                updatedRoomConveniences.add(roomConvenience);
            }
            roomType.setRoomConveniences(updatedRoomConveniences);
        }

        roomTypeRepository.save(roomType);
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return RoomTypeResponse.fromType(savedRoomType);
    }

    @Override
    @Transactional
    public void deleteRoomType(Long id) throws DataNotFoundException {
        RoomType roomType = roomTypeRepository.findWithTypesAndRoomConveniencesById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND));
        roomType.getRoomImages().forEach(roomImageRepository::delete);
        roomTypeRepository.delete(roomType);
    }

    @Override
    public RoomTypeResponse getRoomTypeById(Long id) throws DataNotFoundException {
        RoomType roomType = roomTypeRepository.findWithTypesAndRoomConveniencesById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND));
        return RoomTypeResponse.fromType(roomType);
    }

    @Override
    public List<RoomTypeResponse> filterRoomType(Long hotelId, Boolean luxury, Boolean singleBedroom, Boolean twinBedroom,
                                                 Boolean doubleBedroom, Boolean wardrobe, Boolean airConditioning, Boolean tv, Boolean wifi, Boolean toiletries,
                                                 Boolean kitchen, Double minPrice, Double maxPrice) {

        List<RoomType> roomTypes = roomTypeRepository.findByTypeAndConveniencesAndPriceAndHotel(hotelId, luxury, singleBedroom, twinBedroom, doubleBedroom,
                wardrobe, airConditioning, tv, wifi, toiletries, kitchen, minPrice, maxPrice);

        return roomTypes.stream()
                .map(RoomTypeResponse::fromType)
                .collect(Collectors.toList());
    }

    private RoomType convertToEntity(RoomTypeDTO roomTypeDTO) {
        Type types = convertToTypeEntity(roomTypeDTO.getTypes());

        Hotel hotel = new Hotel();
        hotel.setId(roomTypeDTO.getHotelId());
        Set<RoomConvenience> roomConveniences = roomTypeDTO.getConveniences().stream()
                .map(this::convertToRoomConvenienceEntity)
                .collect(Collectors.toSet());

        Set<RoomImage> roomImages = Collections.emptySet();

        return RoomType.builder()
                .hotel(hotel)
                .numberOfRoom(roomTypeDTO.getNumberOfRooms())
                .roomPrice(roomTypeDTO.getRoomPrice())
                .description(roomTypeDTO.getDescription())
                .status(roomTypeDTO.getStatus())
                .roomImages(roomImages)
                .type(types)
                .roomConveniences(roomConveniences)
                .build();
    }

    private Type convertToTypeEntity(TypeRoomDTO typeRoomDTO) {
        return typeRepository.findByLuxuryAndSingleBedroomAndTwinBedroomAndDoubleBedroom(
                typeRoomDTO.getLuxury(),
                typeRoomDTO.getSingleBedroom(),
                typeRoomDTO.getTwinBedroom(),
                typeRoomDTO.getDoubleBedroom()
        ).orElseGet(() -> createNewType(typeRoomDTO));
    }

    private RoomConvenience convertToRoomConvenienceEntity(ConvenienceRoomDTO convenienceRoomDTO) {
        return convenienceRoomRepository.findByWardrobeAndAirConditioningAndTvAndWifiAndToiletriesAndKitchen(
                convenienceRoomDTO.getWardrobe(),
                convenienceRoomDTO.getAirConditioning(),
                convenienceRoomDTO.getTv(),
                convenienceRoomDTO.getWifi(),
                convenienceRoomDTO.getToiletries(),
                convenienceRoomDTO.getKitchen()
        ).orElseGet(() -> createNewRoomConvenience(convenienceRoomDTO));
    }

    private Type createNewType(TypeRoomDTO typeRoomDTO) {
        Type type = new Type();
        type.setLuxury(typeRoomDTO.getLuxury());
        type.setSingleBedroom(typeRoomDTO.getSingleBedroom());
        type.setTwinBedroom(typeRoomDTO.getTwinBedroom());
        type.setDoubleBedroom(typeRoomDTO.getDoubleBedroom());
        return type;
    }

    private RoomConvenience createNewRoomConvenience(ConvenienceRoomDTO convenienceRoomDTO) {
        RoomConvenience roomConvenience = new RoomConvenience();
        roomConvenience.setWardrobe(convenienceRoomDTO.getWardrobe());
        roomConvenience.setAirConditioning(convenienceRoomDTO.getAirConditioning());
        roomConvenience.setTv(convenienceRoomDTO.getTv());
        roomConvenience.setWifi(convenienceRoomDTO.getWifi());
        roomConvenience.setToiletries(convenienceRoomDTO.getToiletries());
        roomConvenience.setKitchen(convenienceRoomDTO.getKitchen());
        Set<RoomType> roomTypes = new HashSet<>();
        roomConvenience.setRoomTypes(roomTypes);
        return roomConvenience;
    }
}
