package com.chinhbean.bookinghotel.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.chinhbean.bookinghotel.components.LocalizationUtils;
import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.entities.HotelBusinessLicense;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.repositories.IHotelBusinessLicenseRepository;
import com.chinhbean.bookinghotel.repositories.IHotelRepository;
import com.chinhbean.bookinghotel.responses.HotelBusinessLicenseResponse;
import com.chinhbean.bookinghotel.responses.HotelResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class HotelBusinessLicenseService implements IHotelBusinessLicenseService {

    private final IHotelBusinessLicenseRepository IHotelBusinessLicenseRepository;
    private final AmazonS3 amazonS3;
    private final IHotelRepository hotelRepository;
    private final LocalizationUtils localizationUtils;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Value("${amazonProperties.uploadDir}")
    private String uploadDir;


    @Override
    public HotelResponse uploadBusinessLicense(List<MultipartFile> images, Long hotelId) throws IOException, DataNotFoundException {

        List<String> imageUrls = new ArrayList<>();
        List<HotelBusinessLicenseResponse> hotelBusinessLicenseResponses = new ArrayList<>();
        if (hotelRepository.findById(hotelId).isEmpty()) {
            throw new IllegalArgumentException(MessageKeys.HOTEL_DOES_NOT_EXISTS);
        }

        for (MultipartFile image : images) {
            validateImageFile(image);
            String imageName = image.getOriginalFilename();
            String key = uploadDir + hotelId + "/" + imageName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());
            amazonS3.putObject(bucketName, key, image.getInputStream(), metadata);
            String imageUrl = amazonS3.getUrl(bucketName, key).toString();
            // Check if the image URL already exists
            if (IHotelBusinessLicenseRepository.findByBusinessLicenseAndHotelId(imageUrl, hotelId).isPresent()) {
                throw new DuplicateKeyException("License URL already exists for this hotel " + hotelId);
            }
            imageUrls.add(imageUrl);

            // Create RoomImage entity and save it to the database
            HotelBusinessLicense hotelBusinessLicense = HotelBusinessLicense.builder()
                    .businessLicense(imageUrl)
                    .hotel(Hotel.builder().id(hotelId).build()) // Assuming you have a constructor or builder method to set the id of the Room entity
                    .build();
            hotelBusinessLicense = IHotelBusinessLicenseRepository.save(hotelBusinessLicense);

            // Create RoomImageResponse and set id and room_id
            HotelBusinessLicenseResponse hotelBusinessLicenseResponse = HotelBusinessLicenseResponse.builder()
                    .businessLicenseUrl(hotelBusinessLicense.getBusinessLicense())
                    .build();
            hotelBusinessLicenseResponses.add(hotelBusinessLicenseResponse);
        }

        // Create the response object in the desired format
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new IllegalArgumentException(MessageKeys.HOTEL_DOES_NOT_EXISTS));
        HotelResponse hotelResponse = HotelResponse.fromHotel(hotel);
        hotelResponse.setBusinessLicenseUrls(hotelBusinessLicenseResponses); // Set the image URLs
        return hotelResponse;
    }

    private void validateImageFile(MultipartFile imageFile) {
        MediaType mediaType = MediaType.parseMediaType(Objects.requireNonNull(imageFile.getContentType()));
        if (!mediaType.isCompatibleWith(MediaType.IMAGE_JPEG) && !mediaType.isCompatibleWith(MediaType.IMAGE_PNG)) {
            throw new IllegalArgumentException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
        }
    }
}
