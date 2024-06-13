package com.chinhbean.bookinghotel.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.chinhbean.bookinghotel.components.LocalizationUtils;
import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.entities.HotelImages;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.repositories.HotelImageRepository;
import com.chinhbean.bookinghotel.repositories.IHotelRepository;
import com.chinhbean.bookinghotel.responses.HotelImageResponse;
import com.chinhbean.bookinghotel.responses.HotelResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HotelImageService implements IHotelImageService {

    private final HotelImageRepository hotelImageRepository;
    private final AmazonS3 amazonS3;
    private final IHotelRepository hotelRepository;
    private final LocalizationUtils localizationUtils;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${app.hotel.image.directory}")
    private String hotelImageDirectory;

    @Override
    public HotelResponse uploadImages(List<MultipartFile> images, Long hotelId) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        List<HotelImageResponse> hotelImageResponses = new ArrayList<>();
        if (hotelRepository.findById(hotelId).isEmpty()) {
            throw new IllegalArgumentException(MessageKeys.HOTEL_DOES_NOT_EXISTS);
        }

        for (MultipartFile image : images) {
            validateImageFile(image);
            String imageName = image.getOriginalFilename();
            String key = hotelImageDirectory + hotelId + "/" + imageName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());
            amazonS3.putObject(bucketName, key, image.getInputStream(), metadata);
            String imageUrl = amazonS3.getUrl(bucketName, key).toString();
            // Check if the image URL already exists
            if (hotelImageRepository.findByImageUrlAndHotelId(imageUrl, hotelId).isPresent()) {
                throw new DuplicateKeyException("Image URL already exists for this hotel " + hotelId);
            }
            imageUrls.add(imageUrl);

            // Create RoomImage entity and save it to the database
            HotelImages hotelImages = HotelImages.builder()
                    .imageUrl(imageUrl)
                    .hotel(Hotel.builder().id(hotelId).build()) // Assuming you have a constructor or builder method to set the id of the Room entity
                    .build();
            hotelImages = hotelImageRepository.save(hotelImages);

            // Create RoomImageResponse and set id and room_id
            HotelImageResponse hotelImageResponse = HotelImageResponse.builder()
                    .id(hotelImages.getId()) // Set the id
                    .imageUrl(hotelImages.getImageUrl())
                    .hotelId(hotelId) // Set the room_id
                    .build();
            hotelImageResponses.add(hotelImageResponse);
        }

        // Create the response object in the desired format
        Hotel roomType = hotelRepository.findById(hotelId).orElseThrow(() -> new IllegalArgumentException(MessageKeys.HOTEL_DOES_NOT_EXISTS));
        HotelResponse hotelResponse = HotelResponse.fromHotel(roomType);
        hotelResponse.setImageUrls(hotelImageResponses); // Set the image URLs
        return hotelResponse;
    }

    @Override
    public HotelResponse updateHotelImages(Map<Integer, MultipartFile> imageMap, Long hotelId) throws DataNotFoundException, IOException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND));

        List<HotelImageResponse> hotelImageResponses = new ArrayList<>();

        for (Map.Entry<Integer, MultipartFile> entry : imageMap.entrySet()) {
            Integer imageIndex = entry.getKey();
            MultipartFile imageFile = entry.getValue();

            // Validate image file
            validateImageFile(imageFile);

            // Find the room image by index
            Optional<HotelImages> optionalRoomImage = hotelImageRepository.findById(Long.valueOf(imageIndex));

            if (optionalRoomImage.isPresent()) {
                HotelImages existingImage = optionalRoomImage.get();

                // Upload new image to S3
                String imageUrl = uploadImageToS3(imageFile, hotelId);

                // Delete previous image from S3
                deleteImageFromS3(existingImage.getImageUrl());

                // Update existing image URL
                existingImage.setImageUrl(imageUrl);
                hotelImageRepository.save(existingImage);

                // Create RoomImageResponse and add to response list
                HotelImageResponse roomImageResponse = HotelImageResponse.builder()
                        .id(existingImage.getId())
                        .imageUrl(existingImage.getImageUrl())
                        .hotelId(hotelId)
                        .build();
                hotelImageResponses.add(roomImageResponse);
            }
        }

        HotelResponse hotelResponse = HotelResponse.fromHotel(hotel);
        hotelResponse.setImageUrls(hotelImageResponses); // Set the image URLs
        return hotelResponse;
    }

    private String uploadImageToS3(MultipartFile imageFile, Long hotelId) throws IOException {
        String key = generateImageKey(imageFile, hotelId);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());
        amazonS3.putObject(bucketName, key, imageFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucketName, key).toString();
    }

    private String generateImageKey(MultipartFile imageFile, Long hotelId) {
        String imageName = imageFile.getOriginalFilename();
        return hotelImageDirectory + hotelId + "/" + imageName;
    }

    private void validateImageFile(MultipartFile imageFile) {
        MediaType mediaType = MediaType.parseMediaType(Objects.requireNonNull(imageFile.getContentType()));
        if (!mediaType.isCompatibleWith(MediaType.IMAGE_JPEG) && !mediaType.isCompatibleWith(MediaType.IMAGE_PNG)) {
            throw new IllegalArgumentException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
        }
    }

    private void deleteImageFromS3(String imageUrl) {
        try {
            String key = imageUrl.substring(imageUrl.indexOf(bucketName) + bucketName.length() + 1);
            amazonS3.deleteObject(bucketName, key);
        } catch (AmazonS3Exception e) {
            // Log or handle the exception
            System.err.println("Error deleting image from S3: " + e.getMessage());
        }
    }
}
