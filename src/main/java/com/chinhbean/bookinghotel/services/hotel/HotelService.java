package com.chinhbean.bookinghotel.services.hotel;

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
import com.chinhbean.bookinghotel.responses.hotel.HotelResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService implements IHotelService {

    private final IHotelRepository hotelRepository;
    private final LocalizationUtils localizationUtils;
    private final IConvenienceRepository convenienceRepository;
    private static final Logger logger = LoggerFactory.getLogger(HotelService.class);

    @Transactional
    @Override
    public Page<HotelResponse> getAllHotels(int page, int size) {
        logger.info("Fetching all ACTIVE hotels from the database.");
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels = hotelRepository.findAllByStatus(HotelStatus.ACTIVE, pageable);
        if (hotels.isEmpty()) {
            logger.warn("No ACTIVE hotels found in the database.");
            return Page.empty();
        }
        logger.info("Successfully retrieved all ACTIVE hotels.");
        return hotels.map(HotelResponse::fromHotel);
    }

    @Transactional
    @Override
    public Page<HotelResponse> getAdminHotels(int page, int size) {
        logger.info("Fetching all hotels from the database.");
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels = hotelRepository.findAll(pageable);
        if (hotels.isEmpty()) {
            logger.warn("No hotels found in the database.");
            return Page.empty();
        }
        logger.info("Successfully retrieved all hotels.");
        return hotels.map(HotelResponse::fromHotel);
    }

    @Transactional
    @Override
    public Page<HotelResponse> getPartnerHotels(int page, int size, User userDetails) {
        logger.info("Getting hotels for partner with ID: {}", userDetails.getId());
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels = hotelRepository.findHotelsByPartnerId(userDetails.getId(), pageable);
        if (hotels.isEmpty()) {
            logger.warn("No hotels found for the partner with ID: {}", userDetails.getId());
            return Page.empty();
        }
        logger.info("Successfully retrieved all hotels for the partner with ID: {}", userDetails.getId());
        return hotels.map(HotelResponse::fromHotel);
    }


    @Transactional
    @Override
    public HotelResponse getHotelDetail(Long hotelId) throws DataNotFoundException, PermissionDenyException {
        logger.info("Fetching details for hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> {
                    logger.error("Hotel with ID: {} does not exist.", hotelId);
                    return new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS));
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Check if the current user is the owner of the hotel or an admin
        if (!currentUser.getId().equals(hotel.getPartner().getId()) && !currentUser.getRole().getRoleName().equals(Role.ADMIN)) {
            throw new PermissionDenyException(localizationUtils.getLocalizedMessage(MessageKeys.USER_DOES_NOT_HAVE_PERMISSION_TO_VIEW_HOTEL));
        }

        logger.info("Successfully retrieved details for hotel with ID: {}", hotelId);
        return HotelResponse.fromHotel(hotel);
    }

    @Transactional
    @Override
    public HotelResponse createHotel(HotelDTO hotelDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        logger.info("Creating a new hotel with name: {}", hotelDTO.getHotelName());
        Hotel hotel = convertToEntity(hotelDTO);
        hotel.setPartner(currentUser);
        Set<Convenience> newConveniences = hotel.getConveniences().stream()
                .filter(convenience -> convenience.getId() == null)
                .collect(Collectors.toSet());
        convenienceRepository.saveAll(newConveniences);
        Hotel savedHotel = hotelRepository.save(hotel);
        logger.info("Hotel created successfully with ID: {}", savedHotel.getId());
        return HotelResponse.fromHotel(savedHotel);
    }


    private Hotel convertToEntity(HotelDTO hotelDTO) {
        HotelLocation location = new HotelLocation();
        location.setAddress(hotelDTO.getLocation().getAddress());
        location.setProvince(hotelDTO.getLocation().getProvince());
        Set<Convenience> conveniences = hotelDTO.getConveniences().stream()
                .map(this::createNewConvenience)
                .collect(Collectors.toSet());
        Hotel hotel = Hotel.builder()
                .hotelName(hotelDTO.getHotelName())
                .rating(hotelDTO.getRating())
                .description(hotelDTO.getDescription())
                .brand(hotelDTO.getBrand())
                .status(HotelStatus.PENDING)
                .conveniences(conveniences)
                .location(location)
                .build();
        location.setHotel(hotel);
        return hotel;
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
    public HotelResponse updateHotel(Long hotelId, HotelDTO updateDTO) throws DataNotFoundException, PermissionDenyException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS)));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if (hotel.getStatus() == HotelStatus.PENDING) {
            throw new PermissionDenyException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_IS_PENDING));
        }
        if (!currentUser.getId().equals(hotel.getPartner().getId())) {
            throw new PermissionDenyException(localizationUtils.getLocalizedMessage(MessageKeys.USER_DOES_NOT_HAVE_PERMISSION_TO_UPDATE_HOTEL));
        }
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
            location.setProvince(locationDTO.getProvince());
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

    @Transactional
    @Override
    public void updateStatus(Long hotelId, HotelStatus newStatus) throws DataNotFoundException, PermissionDenyException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS)));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if (Role.ADMIN.equals(currentUser.getRole().getRoleName())) {
            hotel.setStatus(newStatus);
        } else if (Role.PARTNER.equals(currentUser.getRole().getRoleName())) {
            if (newStatus == HotelStatus.ACTIVE || newStatus == HotelStatus.INACTIVE || newStatus == HotelStatus.CLOSED) {
                hotel.setStatus(newStatus);
            } else {
                throw new PermissionDenyException(localizationUtils.getLocalizedMessage(MessageKeys.USER_CANNOT_CHANGE_STATUS_TO, newStatus.toString()));
            }
        } else {
            throw new PermissionDenyException(localizationUtils.getLocalizedMessage(MessageKeys.USER_DOES_NOT_HAVE_PERMISSION_TO_CHANGE_STATUS));
        }
        hotelRepository.save(hotel);
    }

    @Override
    public Hotel getHotelById(Long hotelId) throws DataNotFoundException {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.NO_HOTELS_FOUND, hotelId)));
    }

    @Override
    public Page<HotelResponse> findHotelsByProvinceAndDatesAndCapacity(String province, int numPeople, LocalDate checkInDate, LocalDate checkOutDate, int page, int size) {
        if (checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels = hotelRepository.findHotelsByProvinceAndDatesAndCapacity(province, checkInDate, checkOutDate, numPeople, pageable);
        return hotels.map(HotelResponse::fromHotel);
    }

    @Override
    public Page<HotelResponse> filterHotelsByConveniencesAndRating(Integer rating, Boolean freeBreakfast, Boolean pickUpDropOff, Boolean restaurant, Boolean bar, Boolean pool, Boolean freeInternet, Boolean reception24h, Boolean laundry, int page, int size) throws DataNotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels = hotelRepository.filterHotelWithConvenience(rating, freeBreakfast, pickUpDropOff, restaurant, bar, pool, freeInternet, reception24h, laundry, pageable);
        return hotels.map(HotelResponse::fromHotel);
    }

    @Override
    public void deleteHotel(Long hotelId) throws DataNotFoundException {
        if (!hotelRepository.existsById(hotelId)) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS, hotelId));
        }
        hotelRepository.deleteById(hotelId);
    }
}