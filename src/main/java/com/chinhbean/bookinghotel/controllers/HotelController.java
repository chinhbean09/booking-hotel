package com.chinhbean.bookinghotel.controllers;

import com.chinhbean.bookinghotel.dtos.HotelDTO;
import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.responses.HotelResponse;
import com.chinhbean.bookinghotel.responses.ResponseObject;
import com.chinhbean.bookinghotel.services.IHotelImageService;
import com.chinhbean.bookinghotel.services.IHotelService;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/v1/hotels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotelController {
    private final IHotelService hotelService;
    private final IHotelImageService hotelImageService;

    @GetMapping("/partnerHotels")
    @PreAuthorize("hasAnyAuthority('ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> getPartnerHotels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return getHotelsResponse(hotelService.getPartnerHotels(page, size));
    }

    @GetMapping("/getAllHotels")
    public ResponseEntity<ResponseObject> getAllHotels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return getHotelsResponse(hotelService.getAllHotels(page, size));
    }

    private ResponseEntity<ResponseObject> getHotelsResponse(Page<HotelResponse> hotels) {
        if (hotels.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(MessageKeys.NO_HOTELS_FOUND)
                    .build());
        } else {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(hotels)
                    .message(MessageKeys.RETRIEVED_ALL_HOTELS_SUCCESSFULLY)
                    .build());
        }
    }

    @GetMapping("/detail/{hotelId}")
    public ResponseEntity<ResponseObject> getHotelDetail(@PathVariable Long hotelId) {
        try {
            HotelResponse hotelDetail = hotelService.getHotelDetail(hotelId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(hotelDetail)
                    .message(MessageKeys.RETRIEVED_HOTEL_DETAILS_SUCCESSFULLY)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(MessageKeys.NO_HOTELS_FOUND)
                    .build());
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> createHotel(@RequestBody HotelDTO hotelDTO) {
        try {

            HotelResponse createdHotel = hotelService.createHotel(hotelDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .data(createdHotel)
                    .message(MessageKeys.INSERT_HOTEL_SUCCESSFULLY)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/updateHotel/{hotelId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> updateHotel(@PathVariable Long hotelId, @RequestBody HotelDTO hotelDTO) {
        try {
            HotelResponse updatedHotel = hotelService.updateHotel(hotelId, hotelDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(MessageKeys.UPDATE_HOTEL_SUCCESSFULLY)
                    .data(updatedHotel)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(MessageKeys.NO_HOTELS_FOUND)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/updateStatus/{hotelId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> updateHotelStatus(@PathVariable Long hotelId, @RequestBody HotelStatus newStatus) {
        try {
            hotelService.updateStatus(hotelId, newStatus);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(MessageKeys.UPDATE_HOTEL_STATUS_SUCCESSFULLY)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(MessageKeys.NO_HOTELS_FOUND)
                    .build());
        } catch (PermissionDenyException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseObject.builder()
                    .status(HttpStatus.FORBIDDEN)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/upload-images/{hotelId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> uploadRoomImages(@RequestParam("images") List<MultipartFile> images, @PathVariable("hotelId") Long hotelId) throws IOException {
        try {
            HotelResponse hotelImageResponse = hotelImageService.uploadImages(images, hotelId);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .data(hotelImageResponse)
                    .message(MessageKeys.UPLOAD_IMAGES_SUCCESSFULLY)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/update-images/{hotelId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> updateRoomImages(@PathVariable Long hotelId, @RequestParam Map<String, MultipartFile> images) {
        try {
            Map<Integer, MultipartFile> imageMap = images.entrySet().stream()
                    .collect(Collectors.toMap(entry -> Integer.parseInt(entry.getKey()), Map.Entry::getValue));
            HotelResponse updateHotelImages = hotelImageService.updateHotelImages(imageMap, hotelId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(updateHotelImages)
                    .message(MessageKeys.UPDATED_IMAGES_SUCCESSFULLY)
                    .build());
        } catch (DataNotFoundException | IOException | PermissionDenyException e) {
            HttpStatus status = e instanceof DataNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(ResponseObject.builder()
                    .status(status)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PutMapping(value = "/update-business-license/{hotelId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> updateBusinessLicense(@PathVariable long hotelId,
                                                                @RequestParam("license") MultipartFile license) throws DataNotFoundException, IOException, PermissionDenyException {
        Hotel hotel = hotelService.uploadBusinessLicense(hotelId, license);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(HotelResponse.fromHotel(hotel))
                .message(MessageKeys.UPDATE_LICENSE_SUCCESSFULLY)
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseObject> findByProvinceAndCapacityPerRoomAndAvailability(
            @RequestParam String province,
            @RequestParam int numPeople,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkOutDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return getHotelsResponse(hotelService.findByProvinceAndCapacityPerRoomAndAvailability(province, numPeople, checkInDate, checkOutDate, page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<ResponseObject> filterHotels(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Set<Long> convenienceIds,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Boolean luxury,
            @RequestParam(required = false) Boolean singleBedroom,
            @RequestParam(required = false) Boolean twinBedroom,
            @RequestParam(required = false) Boolean doubleBedroom,
            @RequestParam(required = false) Boolean freeBreakfast,
            @RequestParam(required = false) Boolean pickUpDropOff,
            @RequestParam(required = false) Boolean restaurant,
            @RequestParam(required = false) Boolean bar,
            @RequestParam(required = false) Boolean pool,
            @RequestParam(required = false) Boolean freeInternet,
            @RequestParam(required = false) Boolean reception24h,
            @RequestParam(required = false) Boolean laundry,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException("Page and size parameters must be positive.");
            }
            return getHotelsResponse(hotelService.filterHotels(province, rating, convenienceIds, minPrice, maxPrice, luxury, singleBedroom, twinBedroom, doubleBedroom, freeBreakfast, pickUpDropOff, restaurant, bar, pool, freeInternet, reception24h, laundry, typeId, page, size));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(Collections.emptyList())
                    .message(e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/{hotelId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PARTNER')")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId) {
        try {
            hotelService.deleteHotel(hotelId);
            return ResponseEntity.noContent().build();
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
