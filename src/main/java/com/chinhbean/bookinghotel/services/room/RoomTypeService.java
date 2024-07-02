package com.chinhbean.bookinghotel.services.room;

import com.chinhbean.bookinghotel.dtos.ConvenienceRoomDTO;
import com.chinhbean.bookinghotel.dtos.RoomTypeDTO;
import com.chinhbean.bookinghotel.dtos.TypeRoomDTO;
import com.chinhbean.bookinghotel.entities.*;
import com.chinhbean.bookinghotel.enums.RoomTypeStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.repositories.IConvenienceRoomRepository;
import com.chinhbean.bookinghotel.repositories.IRoomImageRepository;
import com.chinhbean.bookinghotel.repositories.IRoomTypeRepository;
import com.chinhbean.bookinghotel.repositories.ITypeRepository;
import com.chinhbean.bookinghotel.responses.room.RoomTypeResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeService implements IRoomTypeService {

    private final ITypeRepository typeRepository;
    private final IRoomTypeRepository IRoomTypeRepository;
    private final IConvenienceRoomRepository convenienceRoomRepository;
    private final IRoomImageRepository roomImageRepository;

    @Override
    @Transactional
    public RoomTypeResponse createRoomType(RoomTypeDTO roomTypeDTO) {
        // Convert DTO to entity
        RoomType roomType = convertToEntity(roomTypeDTO);

        // Save the new Type entity
        Type newType = convertToTypeEntity(roomTypeDTO.getTypes());
        if (isValidType(newType)) {
            Type savedType = typeRepository.save(newType);
            // Assign the saved Type to the RoomType
            roomType.setType(savedType);
        } else {
            throw new IllegalArgumentException("Only one bedroom type can be selected");
        }
        Set<RoomConvenience> newConveniences = roomType.getRoomConveniences().stream()
                .filter(convenience -> convenience.getId() == null)
                .collect(Collectors.toSet());
        convenienceRoomRepository.saveAll(newConveniences);
        //roomType.setRoomConveniences(newConveniences);

        // Save the RoomType
        RoomType savedRoomType = IRoomTypeRepository.save(roomType);

        // Return the RoomType response
        return RoomTypeResponse.fromType(savedRoomType);
    }

    @Override
    public Page<RoomTypeResponse> getAllRoomTypesByHotelId(Long hotelId, int page, int size) throws DataNotFoundException {

        Pageable pageable = PageRequest.of(page, size);
        Page<RoomType> roomTypes = IRoomTypeRepository.findWithTypesAndRoomConveniencesByHotelId(hotelId, pageable);

        if (roomTypes.isEmpty()) {
            throw new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND);
        } else {
            return roomTypes.map(RoomTypeResponse::fromType);
        }
    }
    public Page<RoomTypeResponse> getAvailableRoomTypesByHotelIdAndDates(Long hotelId, LocalDate checkIn, LocalDate checkOut, Pageable pageable) throws DataNotFoundException {
        Page<RoomType> roomTypes = IRoomTypeRepository.findAvailableRoomsByHotelIdAndDates(hotelId, checkIn, checkOut, pageable);
        if (roomTypes.isEmpty()) {
            throw new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND);
        } else {
            return roomTypes.map(RoomTypeResponse::fromType);
        }
    }

    @Override
    @Transactional
    public RoomTypeResponse updateRoomType(Long roomTypeId, RoomTypeDTO roomTypeDTO) throws DataNotFoundException {
        RoomType roomType = IRoomTypeRepository.findById(roomTypeId)
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

        if (roomTypeDTO.getRoomTypeName() != null) {
            roomType.setRoomTypeName(roomTypeDTO.getRoomTypeName());
        }

        if (roomTypeDTO.getCapacityPerRoom() != 0 && roomTypeDTO.getCapacityPerRoom() > 0) {
            roomType.setCapacityPerRoom(roomTypeDTO.getCapacityPerRoom());
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

        IRoomTypeRepository.save(roomType);
        RoomType savedRoomType = IRoomTypeRepository.save(roomType);
        return RoomTypeResponse.fromType(savedRoomType);
    }

    @Override
    @Transactional
    public void deleteRoomType(Long id) throws DataNotFoundException {
        RoomType roomType = IRoomTypeRepository.findWithTypesAndRoomConveniencesById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND));
        roomImageRepository.deleteAll(roomType.getRoomImages());
        IRoomTypeRepository.delete(roomType);
    }

    @Override
    public RoomTypeResponse getRoomTypeById(Long id) throws DataNotFoundException {
        RoomType roomType = IRoomTypeRepository.findWithTypesAndRoomConveniencesById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND));
        return RoomTypeResponse.fromType(roomType);
    }

    @Override
    public List<RoomTypeResponse> filterRoomType(Long hotelId, Boolean luxury, Boolean singleBedroom, Boolean twinBedroom,
                                                 Boolean doubleBedroom, Boolean wardrobe, Boolean airConditioning, Boolean tv, Boolean wifi, Boolean toiletries,
                                                 Boolean kitchen, Double minPrice, Double maxPrice) {

        List<RoomType> roomTypes = IRoomTypeRepository.findByTypeAndConveniencesAndPriceAndHotel(hotelId, luxury, singleBedroom, twinBedroom, doubleBedroom,
                wardrobe, airConditioning, tv, wifi, toiletries, kitchen, minPrice, maxPrice);

        return roomTypes.stream()
                .map(RoomTypeResponse::fromType)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long roomTypeId, RoomTypeStatus newStatus) throws DataNotFoundException {
        RoomType roomType = IRoomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND));
        roomType.setStatus(newStatus);
        IRoomTypeRepository.save(roomType);
    }

    @Override
    public Page<RoomTypeResponse> getAllRoomTypesByStatus(Long hotelId, int page, int size) throws DataNotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomType> roomTypes = IRoomTypeRepository.findAllByStatusAndHotelId(RoomTypeStatus.AVAILABLE, pageable, hotelId);
        if (roomTypes.isEmpty()) {
            throw new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND);
        } else {
            return roomTypes.map(RoomTypeResponse::fromType);
        }
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
                .roomTypeName(roomTypeDTO.getRoomTypeName())
                .capacityPerRoom(roomTypeDTO.getCapacityPerRoom())
                .roomPrice(roomTypeDTO.getRoomPrice())
                .description(roomTypeDTO.getDescription())
                .status(RoomTypeStatus.AVAILABLE)
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

    private boolean isValidType(Type type) {
        int count = 0;
        if (Boolean.TRUE.equals(type.getSingleBedroom())) count++;
        if (Boolean.TRUE.equals(type.getTwinBedroom())) count++;
        if (Boolean.TRUE.equals(type.getDoubleBedroom())) count++;
        return count == 1;
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
