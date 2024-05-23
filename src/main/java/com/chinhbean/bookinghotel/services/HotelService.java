package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.components.LocalizationUtils;
import com.chinhbean.bookinghotel.dtos.ConvenienceDTO;
import com.chinhbean.bookinghotel.dtos.HotelDTO;
import com.chinhbean.bookinghotel.dtos.HotelLocationDTO;
import com.chinhbean.bookinghotel.entities.*;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.repositories.ConvenienceRepository;
import com.chinhbean.bookinghotel.repositories.HotelRepository;
import com.chinhbean.bookinghotel.responses.HotelResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService implements IHotelService {

    private final HotelRepository hotelRepository;
    private final LocalizationUtils localizationUtils;
    private final ConvenienceRepository convenienceRepository;

    private static final Logger logger = LoggerFactory.getLogger(HotelService.class);

    @Override
    public List<HotelResponse> getAllHotels() throws DataNotFoundException {
        logger.info("Fetching all hotels from the database.");
        List<Hotel> hotels = hotelRepository.findAll();
        if (hotels.isEmpty()) {
            logger.warn("No hotels found in the database.");
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS));
        }
        logger.info("Successfully retrieved all hotels.");
        return hotels.stream()
                .map(HotelResponse::fromHotel)
                .collect(Collectors.toList());
    }

    @Override
    public HotelResponse getHotelDetail(Long hotelId) throws DataNotFoundException {
        logger.info("Fetching details for hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> {
                    logger.error("Hotel with ID: {} does not exist.", hotelId);
                    return new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS));
                });
        logger.info("Successfully retrieved details for hotel with ID: {}", hotelId);
        return HotelResponse.fromHotel(hotel);
    }

    @Transactional
    @Override
    public HotelResponse createHotel(HotelDTO hotelDTO) throws DataNotFoundException {
        logger.info("Creating a new hotel with name: {}", hotelDTO.getHotelName());
        Hotel hotel = convertToEntity(hotelDTO);
        Set<Convenience> newConveniences = hotel.getConveniences().stream()
                .filter(convenience -> convenience.getId() == null)
                .collect(Collectors.toSet());
        convenienceRepository.saveAll(newConveniences);
        Hotel savedHotel = hotelRepository.save(hotel);
        logger.info("Hotel created successfully with ID: {}", savedHotel.getId());
        return HotelResponse.fromHotel(savedHotel);
    }


    private Hotel convertToEntity(HotelDTO hotelDTO) throws DataNotFoundException {
        // Create and set the hotel location from DTO
        HotelLocation location = new HotelLocation();
        location.setAddress(hotelDTO.getLocation().getAddress());
        location.setCity(hotelDTO.getLocation().getCity());
        location.setDistrict(hotelDTO.getLocation().getDistrict());

        // Create and set the hotel conveniences from DTO
        Set<Convenience> conveniences = hotelDTO.getConveniences().stream()
                .map(this::convertToConvenienceEntity)
                .collect(Collectors.toSet());
        return Hotel.builder()
                .hotelName(hotelDTO.getHotelName())
                .rating(hotelDTO.getRating())
                .description(hotelDTO.getDescription())
                .brand(hotelDTO.getBrand())
                .status(HotelStatus.PENDING)
                .conveniences(conveniences)
                .location(location)
                .build();
    }

    private Convenience convertToConvenienceEntity(ConvenienceDTO dto) {
        return convenienceRepository.findByFreeBreakfastAndPickUpDropOffAndRestaurantAndBarAndPoolAndFreeInternetAndReception24hAndLaundry(
                        dto.getFreeBreakfast(),
                        dto.getPickUpDropOff(),
                        dto.getRestaurant(),
                        dto.getBar(),
                        dto.getPool(),
                        dto.getFreeInternet(),
                        dto.getReception24h(),
                        dto.getLaundry())
                .orElseGet(() -> createNewConvenience(dto));
    }

    private Convenience createNewConvenience(ConvenienceDTO dto) {
        Convenience convenience = new Convenience();
        convenience.setFreeBreakfast(dto.getFreeBreakfast());
        convenience.setPickUpDropOff(dto.getPickUpDropOff());
        convenience.setRestaurant(dto.getRestaurant());
        convenience.setBar(dto.getBar());
        convenience.setPool(dto.getPool());
        convenience.setFreeInternet(dto.getFreeInternet());
        convenience.setReception24h(dto.getReception24h());
        convenience.setLaundry(dto.getLaundry());
        return convenience;
    }

    @Transactional
    @Override
    public HotelResponse updateHotel(Long hotelId, HotelDTO updateDTO) throws DataNotFoundException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS)));

        if (updateDTO.getHotelName() != null) {
            hotel.setHotelName(updateDTO.getHotelName());
        }
        if (updateDTO.getRating() != null) {
            hotel.setRating(updateDTO.getRating());
        }
        if (updateDTO.getDescription() != null) {
            hotel.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getBrand() != null) {
            hotel.setBrand(updateDTO.getBrand());
        }
        if (updateDTO.getLocation() != null) {
            HotelLocationDTO locationDTO = updateDTO.getLocation();
            HotelLocation location = hotel.getLocation();
            location.setAddress(locationDTO.getAddress());
            location.setCity(locationDTO.getCity());
            location.setDistrict(locationDTO.getDistrict());
        }

        if (updateDTO.getConveniences() != null) {
            Set<Convenience> conveniences = updateDTO.getConveniences().stream()
                    .map(this::convertToConvenienceEntity)
                    .collect(Collectors.toSet());
            hotel.setConveniences(conveniences);
        }

        Hotel updatedHotel = hotelRepository.save(hotel);
        return HotelResponse.fromHotel(updatedHotel);
    }

    @Override
    public void updateStatus(Long hotelId, HotelStatus newStatus, User user) throws DataNotFoundException, PermissionDenyException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException("Hotel not found"));
        String userRole = user.getRole().getRoleName();
        if (Role.ADMIN.equals(userRole)) {
            hotel.setStatus(newStatus);
        } else if (Role.PARTNER.equals(userRole)) {
            if (newStatus == HotelStatus.ACTIVE || newStatus == HotelStatus.INACTIVE) {
                hotel.setStatus(newStatus);
            } else {
                throw new PermissionDenyException("Partner cannot change status to " + newStatus);
            }
        } else {
            throw new PermissionDenyException("User does not have permission to change status");
        }
        hotelRepository.save(hotel);
    }
}
