package com.chinhbean.bookinghotel.services.room;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.chinhbean.bookinghotel.components.LocalizationUtils;
import com.chinhbean.bookinghotel.entities.RoomImage;
import com.chinhbean.bookinghotel.entities.RoomType;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.repositories.IRoomImageRepository;
import com.chinhbean.bookinghotel.repositories.IRoomTypeRepository;
import com.chinhbean.bookinghotel.responses.room.RoomImageResponse;
import com.chinhbean.bookinghotel.responses.room.RoomTypeResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class RoomImageService implements IRoomImageService {
    private final IRoomImageRepository roomImageRepository;
    private final AmazonS3 amazonS3;
    private final IRoomTypeRepository IRoomTypeRepository;
    private final LocalizationUtils localizationUtils;
    private static final Logger logger = LoggerFactory.getLogger(RoomImageService.class);

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Override
    public RoomTypeResponse uploadImages(List<MultipartFile> images, Long roomTypeId) throws IOException {
        if (IRoomTypeRepository.findById(roomTypeId).isEmpty()) {
            throw new IllegalArgumentException(MessageKeys.ROOM_DOES_NOT_EXISTS);
        }

        List<RoomImageResponse> roomImageResponses = new ArrayList<>();


        for (MultipartFile image : images) {
            String imageUrl = uploadImage(image, roomTypeId);
            // Check if the image URL already exists
            if (roomImageRepository.findByImageUrlsAndRoomTypeId(imageUrl, roomTypeId).isPresent()) {
                throw new DuplicateKeyException("Image URL already exists for this room " + roomTypeId);
            }
            // Create RoomImage entity and save it to the database
            RoomImage roomImage = RoomImage.builder()
                    .imageUrls(imageUrl)
                    .roomType(RoomType.builder().id(roomTypeId).build()) // Assuming you have a constructor or builder method to set the id of the Room entity
                    .build();
            roomImage = roomImageRepository.save(roomImage);

            // Create RoomImageResponse and set id and room_id
            RoomImageResponse roomImageResponse = RoomImageResponse.builder()
                    .id(roomImage.getId()) // Set the id
                    .imageUrl(roomImage.getImageUrls())
                    .roomTypeId(roomTypeId) // Set the room_id
                    .build();
            roomImageResponses.add(roomImageResponse);
        }

        // Create the response object in the desired format
        RoomType roomType = IRoomTypeRepository.findById(roomTypeId).orElseThrow(() -> new IllegalArgumentException(MessageKeys.ROOM_TYPE_NOT_FOUND));
        RoomTypeResponse roomTypeResponse = RoomTypeResponse.fromType(roomType);
        roomTypeResponse.setImageUrls(roomImageResponses); // Set the image URLs
        return roomTypeResponse;
    }

    @Override
    @Transactional
    public RoomTypeResponse updateRoomImages(Map<Integer, MultipartFile> imageMap, Long roomTypeId) throws DataNotFoundException, IOException {
        RoomType roomType = IRoomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_TYPE_NOT_FOUND));

        List<RoomImageResponse> roomImageResponses = new ArrayList<>();

        for (Map.Entry<Integer, MultipartFile> entry : imageMap.entrySet()) {
            Integer imageIndex = entry.getKey();
            MultipartFile imageFile = entry.getValue();

            if (imageIndex == null) {
                throw new IllegalArgumentException("Image index cannot be null");
            }
            // Validate image file
            validateImageFile(imageFile);

            // Find the room image by index
            Optional<RoomImage> optionalRoomImage = roomImageRepository.findById(Long.valueOf(imageIndex));

            if (optionalRoomImage.isPresent()) {
                RoomImage existingImage = optionalRoomImage.get();

                // Delete previous image from S3
                deleteImageFromS3(existingImage.getImageUrls());

                // Upload new image to S3
                String imageUrl = uploadImage(imageFile, roomTypeId);

                // Update existing image URL
                existingImage.setImageUrls(imageUrl);
                roomImageRepository.save(existingImage);

                // Create RoomImageResponse and add to response list
                RoomImageResponse roomImageResponse = RoomImageResponse.builder()
                        .id(existingImage.getId())
                        .imageUrl(existingImage.getImageUrls())
                        .roomTypeId(roomTypeId)
                        .build();
                roomImageResponses.add(roomImageResponse);
            }
        }

        RoomTypeResponse roomTypeResponse = RoomTypeResponse.fromType(roomType);
        roomTypeResponse.setImageUrls(roomImageResponses); // Set the image URLs
        return roomTypeResponse;
    }


    private String uploadImage(MultipartFile image, Long roomId) throws IOException {
        validateImageFile(image);
        String key = getImageKey(image, roomId);
        String imageUrl = amazonS3.getUrl(bucketName, key).toString();
        // Check if the image URL already exists before uploading to S3
        if (roomImageRepository.findByImageUrlsAndRoomTypeId(imageUrl, roomId).isPresent()) {
            throw new DuplicateKeyException("Image URL already exists for this room " + roomId);
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());
        amazonS3.putObject(bucketName, key, image.getInputStream(), metadata);
        return imageUrl;
    }

    private void validateImageFile(MultipartFile imageFile) {
        MediaType mediaType = MediaType.parseMediaType(Objects.requireNonNull(imageFile.getContentType()));
        if (!mediaType.isCompatibleWith(MediaType.IMAGE_JPEG) && !mediaType.isCompatibleWith(MediaType.IMAGE_PNG) && !mediaType.isCompatibleWith(MediaType.IMAGE_GIF)) {
            throw new IllegalArgumentException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
        }
    }

    private void deleteImageFromS3(String imageUrl) {
        try {
            String key = imageUrl.substring(imageUrl.indexOf(bucketName) + bucketName.length() + 1);
            amazonS3.deleteObject(bucketName, key);
            if (amazonS3.doesObjectExist(bucketName, key)) {
                throw new AmazonS3Exception("Failed to delete image from S3");
            }
        } catch (AmazonS3Exception e) {
            logger.error("Error deleting image from S3: ", e);
        }
    }


    private String getImageKey(MultipartFile image, Long roomId) {
        String imageName = image.getOriginalFilename();
        return "room_images/" + roomId + "/" + imageName;
    }

}
