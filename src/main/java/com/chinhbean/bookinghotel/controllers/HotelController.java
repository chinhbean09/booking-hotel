package com.chinhbean.bookinghotel.controllers;

import com.chinhbean.bookinghotel.dtos.HotelDTO;
import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.responses.HotelResponse;
import com.chinhbean.bookinghotel.responses.ResponseObject;
import com.chinhbean.bookinghotel.services.IHotelService;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("api/v1/hotels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotelController {
    private final IHotelService hotelService;

    @GetMapping("/getAllHotels")
    public ResponseEntity<ResponseObject> getAllHotels(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String token = authHeader.substring(7);
        Page<HotelResponse> hotels = hotelService.getAllHotels(token, page, size);
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

    @SecurityRequirement(name = "bearer-key")
    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createHotel(@RequestBody HotelDTO hotelDTO, @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Invalid token");
            }
            String token = authHeader.substring(7);
            HotelResponse createdHotel = hotelService.createHotel(hotelDTO, token);
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

    @SecurityRequirement(name = "bearer-key")
    @PutMapping("/updateHotel/{hotelId}")
    public ResponseEntity<ResponseObject> updateHotel(@PathVariable Long hotelId, @RequestBody HotelDTO hotelDTO, @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Invalid token");
            }
            String token = authHeader.substring(7);
            HotelResponse updatedHotel = hotelService.updateHotel(hotelId, hotelDTO, token);
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

    @SecurityRequirement(name = "bearer-key")
    @PutMapping("/updateStatus/{hotelId}")
    public ResponseEntity<ResponseObject> updateHotelStatus(@PathVariable Long hotelId, @RequestBody HotelStatus newStatus, @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Invalid token");
            }
            String token = authHeader.substring(7);
            hotelService.updateStatus(hotelId, newStatus, token);
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

    @PutMapping(value = "/update-business-license/{hotelId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> updateBusinessLicense(@PathVariable long hotelId,
                                                                @RequestParam("license") MultipartFile license) throws DataNotFoundException, IOException {
        Hotel hotel = hotelService.uploadBusinessLicense(hotelId, license);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(HotelResponse.fromHotel(hotel))
                .message(MessageKeys.UPDATE_LICENSE_SUCCESSFULLY)
                .build());
    }
}
