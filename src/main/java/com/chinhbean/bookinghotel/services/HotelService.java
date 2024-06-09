package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.components.JwtTokenUtils;
import com.chinhbean.bookinghotel.components.LocalizationUtils;
import com.chinhbean.bookinghotel.dtos.ConvenienceDTO;
import com.chinhbean.bookinghotel.dtos.HotelDTO;
import com.chinhbean.bookinghotel.dtos.HotelLocationDTO;
import com.chinhbean.bookinghotel.entities.*;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.repositories.IConvenienceRepository;
import com.chinhbean.bookinghotel.repositories.IHotelRepository;
import com.chinhbean.bookinghotel.repositories.UserRepository;
import com.chinhbean.bookinghotel.responses.HotelResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService implements IHotelService {

    private final IHotelRepository IHotelRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final LocalizationUtils localizationUtils;
    private final IConvenienceRepository IConvenienceRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(HotelService.class);

    @Transactional
    @Override
    public List<HotelResponse> getAllHotels() throws DataNotFoundException {
        logger.info("Fetching all hotels from the database.");
        List<Hotel> hotels = IHotelRepository.findAll();
        if (hotels.isEmpty()) {
            logger.warn("No hotels found in the database.");
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS));
        }
        logger.info("Successfully retrieved all hotels.");
        return hotels.stream()
                .map(HotelResponse::fromHotel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public HotelResponse getHotelDetail(Long hotelId) throws DataNotFoundException {
        logger.info("Fetching details for hotel with ID: {}", hotelId);
        Hotel hotel = IHotelRepository.findById(hotelId)
                .orElseThrow(() -> {
                    logger.error("Hotel with ID: {} does not exist.", hotelId);
                    return new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS));
                });
        logger.info("Successfully retrieved details for hotel with ID: {}", hotelId);
        return HotelResponse.fromHotel(hotel);
    }

    @Transactional
    @Override
    public HotelResponse createHotel(HotelDTO hotelDTO, String token) throws DataNotFoundException {
        User user = getUserDetailsFromToken(token);

        logger.info("Creating a new hotel with name: {}", hotelDTO.getHotelName());
        Hotel hotel = convertToEntity(hotelDTO);
        hotel.setPartner(user);
        Set<Convenience> newConveniences = hotel.getConveniences().stream()
                .filter(convenience -> convenience.getId() == null)
                .collect(Collectors.toSet());
        IConvenienceRepository.saveAll(newConveniences);
        Hotel savedHotel = IHotelRepository.save(hotel);
        logger.info("Hotel created successfully with ID: {}", savedHotel.getId());
        return HotelResponse.fromHotel(savedHotel);
    }


    private Hotel convertToEntity(HotelDTO hotelDTO) {
        HotelLocation location = new HotelLocation();
        location.setAddress(hotelDTO.getLocation().getAddress());
        location.setCity(hotelDTO.getLocation().getCity());
        location.setDistrict(hotelDTO.getLocation().getDistrict());
        Set<Convenience> conveniences = hotelDTO.getConveniences().stream()
                .map(this::convertToConvenienceEntity)
                .collect(Collectors.toSet());
        Set<HotelImages> hotelImages = Collections.emptySet();
        Hotel hotel = Hotel.builder()
                .hotelName(hotelDTO.getHotelName())
                .rating(hotelDTO.getRating())
                .description(hotelDTO.getDescription())
                .brand(hotelDTO.getBrand())
                .status(HotelStatus.PENDING)
                .conveniences(conveniences)
                .location(location)
                .hotelImages(hotelImages)
                .build();
        location.setHotel(hotel);
        return hotel;
    }

    private Convenience convertToConvenienceEntity(ConvenienceDTO dto) {
        return IConvenienceRepository.findByFreeBreakfastAndPickUpDropOffAndRestaurantAndBarAndPoolAndFreeInternetAndReception24hAndLaundry(
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
    public HotelResponse updateHotel(Long hotelId, HotelDTO updateDTO, String token) throws DataNotFoundException {
        User user = getUserDetailsFromToken(token);

        Hotel hotel = IHotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS)));

        hotel.setPartner(user);
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

        Hotel updatedHotel = IHotelRepository.save(hotel);
        return HotelResponse.fromHotel(updatedHotel);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws DataNotFoundException {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.TOKEN_IS_EXPIRED));
        }
        Long id = jwtTokenUtils.extractUserId(token);
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_DOES_NOT_EXISTS)));
    }

    @Transactional
    @Override
    public void updateStatus(Long hotelId, HotelStatus newStatus, String token) throws DataNotFoundException, PermissionDenyException {
        Hotel hotel = IHotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS)));
        String userRole = jwtTokenUtils.extractUserRole(token);
        if (Role.ADMIN.equals(userRole)) {
            hotel.setStatus(newStatus);
        } else if (Role.PARTNER.equals(userRole)) {
            if (newStatus == HotelStatus.ACTIVE || newStatus == HotelStatus.INACTIVE) {
                hotel.setStatus(newStatus);
            } else {
                throw new PermissionDenyException(localizationUtils.getLocalizedMessage(MessageKeys.PARTNER_CANNOT_CHANGE_STATUS_TO, newStatus.toString()));
            }
        } else {
            throw new PermissionDenyException(localizationUtils.getLocalizedMessage(MessageKeys.USER_DOES_NOT_HAVE_PERMISSION_TO_CHANGE_STATUS));
        }
        IHotelRepository.save(hotel);
    }
}
