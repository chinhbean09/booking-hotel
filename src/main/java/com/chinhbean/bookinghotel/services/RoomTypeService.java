package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.RoomTypeDTO;
import com.chinhbean.bookinghotel.dtos.TypeRoomDTO;
import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.entities.RoomImage;
import com.chinhbean.bookinghotel.entities.RoomType;
import com.chinhbean.bookinghotel.entities.Type;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.repositories.RoomTypeRepository;
import com.chinhbean.bookinghotel.repositories.TypeRepository;
import com.chinhbean.bookinghotel.responses.RoomTypeResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeService implements IRoomTypeService {

    private final TypeRepository typeRepository;
    private final RoomTypeRepository roomTypeRepository;

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

        // Save the RoomType
        RoomType savedRoomType = roomTypeRepository.save(roomType);

        // Return the RoomType response
        return RoomTypeResponse.fromType(savedRoomType);
    }


    @Override
    public List<RoomTypeResponse> getAllRoomTypesByHotelId(Long hotelId) throws DataNotFoundException {
        List<RoomType> roomTypes = roomTypeRepository.findWithTypesByHotelId(hotelId);

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

        roomTypeRepository.save(roomType);
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return RoomTypeResponse.fromType(savedRoomType);
    }

    @Override
    public void deleteRoomType(Long id) throws DataNotFoundException {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND));
        roomTypeRepository.delete(roomType);
    }

    private RoomType convertToEntity(RoomTypeDTO roomTypeDTO) {
        Type types = convertToTypeEntity(roomTypeDTO.getTypes());

        Hotel hotel = new Hotel();
        hotel.setId(roomTypeDTO.getHotelId());

        Set<RoomImage> roomImages = Collections.emptySet();

        return RoomType.builder()
                .hotel(hotel)
                .numberOfRoom(roomTypeDTO.getNumberOfRooms())
                .roomPrice(roomTypeDTO.getRoomPrice())
                .description(roomTypeDTO.getDescription())
                .status(roomTypeDTO.getStatus())
                .roomImages(roomImages)
                .type(types)
                .build();
    }

    private Type convertToTypeEntity(TypeRoomDTO typeRoomDTO) {
        return typeRepository.findByLuxuryAndSingleBedroomAndTwinBedroomAndDoubleBedroomAndWardrobeAndAirConditioningAndTvAndWifiAndToiletriesAndKitchen(
                        typeRoomDTO.getLuxury(),
                        typeRoomDTO.getSingleBedroom(),
                        typeRoomDTO.getTwinBedroom(),
                        typeRoomDTO.getDoubleBedroom(),
                        typeRoomDTO.getWardrobe(),
                        typeRoomDTO.getAirConditioning(),
                        typeRoomDTO.getTv(),
                        typeRoomDTO.getWifi(),
                        typeRoomDTO.getToiletries(),
                        typeRoomDTO.getKitchen())
                .orElseGet(() -> createNewType(typeRoomDTO));
    }

    private Type createNewType(TypeRoomDTO typeRoomDTO) {
        Type type = new Type();
        type.setLuxury(typeRoomDTO.getLuxury());
        type.setSingleBedroom(typeRoomDTO.getSingleBedroom());
        type.setTwinBedroom(typeRoomDTO.getTwinBedroom());
        type.setDoubleBedroom(typeRoomDTO.getDoubleBedroom());
        type.setWardrobe(typeRoomDTO.getWardrobe());
        type.setAirConditioning(typeRoomDTO.getAirConditioning());
        type.setTv(typeRoomDTO.getTv());
        type.setWifi(typeRoomDTO.getWifi());
        type.setToiletries(typeRoomDTO.getToiletries());
        type.setKitchen(typeRoomDTO.getKitchen());
        return type;
    }
}
