package com.chinhbean.bookinghotel.services.hotel;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.chinhbean.bookinghotel.components.LocalizationUtils;
import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.entities.HotelImages;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.repositories.IHotelImageRepository;
import com.chinhbean.bookinghotel.repositories.IHotelRepository;
import com.chinhbean.bookinghotel.responses.hotel.HotelImageResponse;
import com.chinhbean.bookinghotel.responses.hotel.HotelResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HotelImageService implements IHotelImageService {

    private final IHotelImageRepository IHotelImageRepository;
    private final AmazonS3 amazonS3;
    private final IHotelRepository hotelRepository;
    private final IHotelService hotelService;
    private final LocalizationUtils localizationUtils;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${amazonProperties.imagePath}")
    private String imagePath;

    @Override
    public HotelResponse uploadImages(List<MultipartFile> images, Long hotelId) throws IOException, PermissionDenyException, DataNotFoundException {
        Hotel hotel = hotelService.getHotelById(hotelId);
        validateUserPermission(hotel);

        List<HotelImageResponse> hotelImageResponses = new ArrayList<>();
        validateHotelExists(hotelId);

        for (MultipartFile image : images) {
            validateImageFile(image);
            String imageUrl = uploadImageToS3(image, hotelId);
            validateImageUrl(imageUrl, hotelId);
            HotelImages hotelImages = saveImageToDatabase(imageUrl, hotelId);
            hotelImageResponses.add(buildHotelImageResponse(hotelImages, hotelId));
        }
        return buildHotelResponse(hotelId, hotelImageResponses);
    }

    @Override
    public HotelResponse updateHotelImages(Map<Integer, MultipartFile> imageMap, Long hotelId) throws IOException, PermissionDenyException, DataNotFoundException {
        Hotel hotel = hotelService.getHotelById(hotelId);
        validateUserPermission(hotel);

        List<HotelImageResponse> hotelImageResponses = new ArrayList<>();

        for (Map.Entry<Integer, MultipartFile> entry : imageMap.entrySet()) {
            Integer imageIndex = entry.getKey();
            MultipartFile imageFile = entry.getValue();

            validateImageFile(imageFile);

            Optional<HotelImages> optionalRoomImage = IHotelImageRepository.findById(Long.valueOf(imageIndex));

            if (optionalRoomImage.isPresent()) {
                HotelImages existingImage = optionalRoomImage.get();

                String imageUrl = uploadImageToS3(imageFile, hotelId);
                deleteImageFromS3(existingImage.getImageUrl());
                existingImage.setImageUrl(imageUrl);
                IHotelImageRepository.save(existingImage);
                hotelImageResponses.add(buildHotelImageResponse(existingImage, hotelId));
            }
        }

        return buildHotelResponse(hotelId, hotelImageResponses);
    }

    private void validateUserPermission(Hotel hotel) throws PermissionDenyException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.getId().equals(hotel.getPartner().getId())) {
            throw new PermissionDenyException(MessageKeys.USER_DOES_NOT_HAVE_PERMISSION_TO_UPDATE_HOTEL);
        }
    }

    private void validateHotelExists(Long hotelId) {
        if (hotelRepository.findById(hotelId).isEmpty()) {
            throw new IllegalArgumentException(MessageKeys.HOTEL_DOES_NOT_EXISTS);
        }
    }

    private void validateImageFile(MultipartFile imageFile) {
        MediaType mediaType = MediaType.parseMediaType(Objects.requireNonNull(imageFile.getContentType()));
        if (!mediaType.isCompatibleWith(MediaType.IMAGE_JPEG) && !mediaType.isCompatibleWith(MediaType.IMAGE_PNG)) {
            throw new IllegalArgumentException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
        }
    }

    private String uploadImageToS3(MultipartFile imageFile, Long hotelId) throws IOException {
        String imageName = imageFile.getOriginalFilename();
        String key = imagePath + hotelId + "/" + imageName;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());
        amazonS3.putObject(bucketName, key, imageFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucketName, key).toString();
    }

    private void validateImageUrl(String imageUrl, Long hotelId) {
        if (IHotelImageRepository.findByImageUrlAndHotelId(imageUrl, hotelId).isPresent()) {
            throw new DuplicateKeyException("Image URL already exists for this hotel " + hotelId);
        }
    }

    private HotelImages saveImageToDatabase(String imageUrl, Long hotelId) {
        HotelImages hotelImages = HotelImages.builder()
                .imageUrl(imageUrl)
                .hotel(Hotel.builder().id(hotelId).build())
                .build();
        return IHotelImageRepository.save(hotelImages);
    }

    private HotelImageResponse buildHotelImageResponse(HotelImages hotelImages, Long hotelId) {
        return HotelImageResponse.builder()
                .id(hotelImages.getId())
                .imageUrl(hotelImages.getImageUrl())
                .hotelId(hotelId)
                .build();
    }

    private HotelResponse buildHotelResponse(Long hotelId, List<HotelImageResponse> hotelImageResponses) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new IllegalArgumentException(MessageKeys.HOTEL_DOES_NOT_EXISTS));
        HotelResponse hotelResponse = HotelResponse.fromHotel(hotel);
        hotelResponse.setImageUrls(hotelImageResponses);
        return hotelResponse;
    }

    private void deleteImageFromS3(String imageUrl) {
        try {
            String key = imageUrl.substring(imageUrl.indexOf(bucketName) + bucketName.length() + 1);
            amazonS3.deleteObject(bucketName, key);
        } catch (AmazonS3Exception e) {
            System.err.println("Error deleting image from S3: " + e.getMessage());
        }
    }
}