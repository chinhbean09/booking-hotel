package com.chinhbean.bookinghotel.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.chinhbean.bookinghotel.components.LocalizationUtils;
import com.chinhbean.bookinghotel.entities.Room;
import com.chinhbean.bookinghotel.entities.RoomImage;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.InvalidParamException;
import com.chinhbean.bookinghotel.repositories.RoomImageRepository;
import com.chinhbean.bookinghotel.repositories.RoomRepository;
import com.chinhbean.bookinghotel.responses.RoomImageResponse;
import com.chinhbean.bookinghotel.responses.RoomResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RoomImageService implements IRoomImageService {
    private final RoomImageRepository roomImageRepository;
    private final AmazonS3 amazonS3;
    private final RoomRepository roomRepository;
    private final LocalizationUtils localizationUtils;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Override
    public List<RoomResponse> uploadImages(List<MultipartFile> images, Long roomId) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        List<RoomImageResponse> roomImageResponses = new ArrayList<>();
        if (roomRepository.findById(roomId).isEmpty()) {
            throw new IllegalArgumentException(MessageKeys.ROOM_DOES_NOT_EXISTS);
        }

        for (MultipartFile image : images) {
            validateImageFile(image);
            String imageName = image.getOriginalFilename();
            String key = "room_images/" + roomId + "/" + imageName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());
            amazonS3.putObject(bucketName, key, image.getInputStream(), metadata);
            String imageUrl = amazonS3.getUrl(bucketName, key).toString();
            // Check if the image URL already exists
            if (roomImageRepository.findByImageUrlsAndRoomId(imageUrl, roomId).isPresent()) {
                throw new DuplicateKeyException("Image URL already exists for this room "+ roomId);
            }
            imageUrls.add(imageUrl);

            // Create RoomImage entity and save it to the database
            RoomImage roomImage = RoomImage.builder()
                    .imageUrls(imageUrl)
                    .room(Room.builder().id(roomId).build()) // Assuming you have a constructor or builder method to set the id of the Room entity
                    .build();
            roomImage = roomImageRepository.save(roomImage);

            // Create RoomImageResponse and set id and room_id
            RoomImageResponse roomImageResponse = RoomImageResponse.builder()
                    .id(roomImage.getId()) // Set the id
                    .imageUrl(roomImage.getImageUrls())
                    .roomId(roomId) // Set the room_id
                    .build();
            roomImageResponses.add(roomImageResponse);
        }

        // Create the response object in the desired format
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException(MessageKeys.ROOM_DOES_NOT_EXISTS));
        return Collections.singletonList(RoomResponse.fromRoom(room));
    }

    @Override
    @Transactional
    public List<RoomResponse> updateRoomImages(Map<Integer, MultipartFile> imageMap, Long roomId) throws DataNotFoundException, IOException {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXISTS));

        List<RoomImageResponse> roomImageResponses = new ArrayList<>();

        for (Map.Entry<Integer, MultipartFile> entry : imageMap.entrySet()) {
            Integer imageIndex = entry.getKey();
            MultipartFile imageFile = entry.getValue();

            // Validate image file
            validateImageFile(imageFile);

            // Find the room image by index
            Optional<RoomImage> optionalRoomImage = roomImageRepository.findById(Long.valueOf(imageIndex));

            if (optionalRoomImage.isPresent()) {
                RoomImage existingImage = optionalRoomImage.get();

                // Upload new image to S3
                String imageUrl = uploadImageToS3(imageFile, roomId);

                // Delete previous image from S3
                deleteImageFromS3(existingImage.getImageUrls());

                // Update existing image URL
                existingImage.setImageUrls(imageUrl);
                roomImageRepository.save(existingImage);

                // Create RoomImageResponse and add to response list
                RoomImageResponse roomImageResponse = RoomImageResponse.builder()
                        .id(existingImage.getId())
                        .imageUrl(existingImage.getImageUrls())
                        .roomId(roomId)
                        .build();
                roomImageResponses.add(roomImageResponse);
            }
        }

        // Convert updated room entity to response
        RoomResponse updatedRoom = RoomResponse.fromRoom(room);

        // Return the updated room response
        return Collections.singletonList(updatedRoom);
    }


    private String uploadImageToS3(MultipartFile imageFile, Long roomId) throws IOException {
        String imageName = imageFile.getOriginalFilename();
        String key = "room_images/" + roomId + "/" + imageName;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());
        amazonS3.putObject(bucketName, key, imageFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucketName, key).toString();
    }

    private void validateImageFile(MultipartFile imageFile) {
        MediaType mediaType = MediaType.parseMediaType(Objects.requireNonNull(imageFile.getContentType()));
        if (!mediaType.isCompatibleWith(MediaType.IMAGE_JPEG) && !mediaType.isCompatibleWith(MediaType.IMAGE_PNG)) {
            throw new IllegalArgumentException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
        }
    }

    private void deleteImageFromS3(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/");
            String key = parts[parts.length - 1];
            amazonS3.deleteObject(bucketName, key);
        } catch (AmazonS3Exception e) {
            // Log or handle the exception
            System.err.println("Error deleting image from S3: " + e.getMessage());
        }
    }

}