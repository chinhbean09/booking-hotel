package com.chinhbean.bookinghotel.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.chinhbean.bookinghotel.components.JwtTokenUtils;
import com.chinhbean.bookinghotel.components.LocalizationUtils;
import com.chinhbean.bookinghotel.dtos.ConvenienceDTO;
import com.chinhbean.bookinghotel.dtos.HotelDTO;
import com.chinhbean.bookinghotel.dtos.HotelLocationDTO;
import com.chinhbean.bookinghotel.entities.*;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.InvalidParamException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService implements IHotelService {

    private final IHotelRepository hotelRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final LocalizationUtils localizationUtils;
    private final IConvenienceRepository convenienceRepository;
    private final UserRepository userRepository;
    private final AmazonS3 amazonS3;

    private static final Logger logger = LoggerFactory.getLogger(HotelService.class);
    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${app.business.license.directory}")
    private String businessLicenseDirectory;

    @Transactional
    @Override
    public Page<HotelResponse> getAllHotels(String token, int page, int size) {
        logger.info("Fetching all hotels from the database.");
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels;
        String userRole = jwtTokenUtils.extractUserRole(token);
        if (Role.PARTNER.equals(userRole)) {
            Long userId = jwtTokenUtils.extractUserId(token);
            hotels = hotelRepository.findAllByPartnerId(userId, pageable);
        } else {
            hotels = hotelRepository.findAll(pageable);
        }
        if (hotels.isEmpty()) {
            logger.warn("No hotels found in the database.");
            return Page.empty();
        }
        logger.info("Successfully retrieved all hotels.");
        return hotels.map(HotelResponse::fromHotel);
    }

    @Transactional
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
    public HotelResponse createHotel(HotelDTO hotelDTO, String token) throws DataNotFoundException {
        User user = getUserDetailsFromToken(token);

        logger.info("Creating a new hotel with name: {}", hotelDTO.getHotelName());
        Hotel hotel = convertToEntity(hotelDTO);
        hotel.setPartner(user);
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
                .map(this::convertToConvenienceEntity)
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
    public HotelResponse updateHotel(Long hotelId, HotelDTO updateDTO, String token) throws DataNotFoundException, PermissionDenyException {
        User user = getUserDetailsFromToken(token);

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS)));

        if (hotel.getStatus() == HotelStatus.PENDING) {
            throw new PermissionDenyException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_IS_PENDING));
        }

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
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.HOTEL_DOES_NOT_EXISTS)));
        String userRole = jwtTokenUtils.extractUserRole(token);
        if (Role.ADMIN.equals(userRole)) {
            hotel.setStatus(newStatus);
        } else if (Role.PARTNER.equals(userRole)) {
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
    public Hotel uploadBusinessLicense(Long hotelId, MultipartFile file) throws IOException, DataNotFoundException {
        Hotel hotel = getHotelById(hotelId);
        validateFile(file);
        String objectKey = buildObjectKey(hotel.getId(), file.getOriginalFilename());
        ObjectMetadata metadata = createObjectMetadata(file);
        uploadFileToS3(bucketName, objectKey, file, metadata);
        String licenseUrl = amazonS3.getUrl(bucketName, objectKey).toString();
        hotel.setBusinessLicense(licenseUrl);
        return hotelRepository.save(hotel);
    }

    private Hotel getHotelById(Long hotelId) throws DataNotFoundException {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.NO_HOTELS_FOUND, hotelId)));
    }

    private void validateFile(MultipartFile file) {
        MediaType mediaType = MediaType.parseMediaType(Objects.requireNonNull(file.getContentType()));
        if (!mediaType.isCompatibleWith(MediaType.IMAGE_JPEG) &&
                !mediaType.isCompatibleWith(MediaType.IMAGE_PNG)) {
            throw new InvalidParamException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
        }
    }

    private String buildObjectKey(Long hotelId, String originalFileName) {
        return businessLicenseDirectory + hotelId + "/" + originalFileName;
    }

    private void uploadFileToS3(String bucketName, String objectKey, MultipartFile file, ObjectMetadata metadata) throws IOException {
        amazonS3.putObject(bucketName, objectKey, file.getInputStream(), metadata);
    }

    private ObjectMetadata createObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        return metadata;
    }

    @Override
    public Page<Hotel> findByProvinceAndCapacityPerRoomAndAvailability(String province, int numPeople, Date checkInDate, Date checkOutDate, int page, int size) {
        if (checkInDate.after(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }
        Pageable pageable = PageRequest.of(page, size);
        return hotelRepository.findByProvinceAndCapacityPerRoomAndAvailability(province, numPeople, checkInDate, checkOutDate, pageable);
    }

    @Override
    public Page<Hotel> filterHotels(String province, Integer rating, Set<Long> convenienceIds, Long typeId, Boolean luxury, Boolean singleBedroom, Boolean twinBedroom, Boolean doubleBedroom, Boolean freeBreakfast, Boolean pickUpDropOff, Boolean restaurant, Boolean bar, Boolean pool, Boolean freeInternet, Boolean reception24h, Boolean laundry, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return hotelRepository.filterHotels(province, rating, convenienceIds, typeId, luxury, singleBedroom, twinBedroom, doubleBedroom, freeBreakfast, pickUpDropOff, restaurant, bar, pool, freeInternet, reception24h, laundry, pageable);
    }
}