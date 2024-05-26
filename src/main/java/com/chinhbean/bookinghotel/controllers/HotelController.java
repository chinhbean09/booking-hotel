package com.chinhbean.bookinghotel.controllers;

import com.chinhbean.bookinghotel.dtos.HotelDTO;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.responses.HotelResponse;
import com.chinhbean.bookinghotel.responses.ResponseObject;
import com.chinhbean.bookinghotel.services.IHotelService;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/hotels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotelController {
    private final IHotelService hotelService;

    @GetMapping("/getListHotels")
    public ResponseEntity<ResponseObject> getAllHotels() throws DataNotFoundException {
        List<HotelResponse> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(hotels)
                .message(MessageKeys.RETRIEVED_ALL_HOTELS_SUCCESSFULLY)
                .build());
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
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createHotel(@RequestBody HotelDTO hotelDTO, @RequestHeader("Authorization") String authHeader) {
        try {
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

    @PutMapping("updateHotel/{hotelId}")
    public ResponseEntity<ResponseObject> updateHotel(@PathVariable Long hotelId, @RequestBody HotelDTO hotelDTO, @RequestHeader("Authorization") String authHeader) {
        try {
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
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }


    @PutMapping("/updateStatus/{hotelId}")
    public ResponseEntity<ResponseObject> updateHotelStatus(@PathVariable Long hotelId, @RequestBody HotelStatus newStatus, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            hotelService.updateStatus(hotelId, newStatus, token);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(MessageKeys.UPDATE_HOTEL_STATUS_SUCCESSFULLY)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
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
}
