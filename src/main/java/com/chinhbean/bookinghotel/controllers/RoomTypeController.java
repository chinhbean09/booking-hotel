package com.chinhbean.bookinghotel.controllers;


import com.chinhbean.bookinghotel.dtos.RoomTypeDTO;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.responses.ResponseObject;
import com.chinhbean.bookinghotel.responses.RoomTypeResponse;
import com.chinhbean.bookinghotel.services.IRoomImageService;
import com.chinhbean.bookinghotel.services.IRoomTypeService;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final IRoomTypeService roomTypeService;
    private final IRoomImageService roomImageService;

    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createRoomType(@RequestBody RoomTypeDTO roomTypeDTO) {
        try {
            RoomTypeResponse createdRoomType = roomTypeService.createRoomType(roomTypeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .data(createdRoomType)
                    .message(MessageKeys.INSERT_ROOM_TYPE_SUCCESSFULLY)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/get-all-by-hotel/{hotelId}")
    public ResponseEntity<ResponseObject> getAllRoomTypesByHotelId(@PathVariable Long hotelId) {
        try {
            List<RoomTypeResponse> roomTypes = roomTypeService.getAllRoomTypesByHotelId(hotelId);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(roomTypes)
                    .message(MessageKeys.RETRIEVED_ROOM_TYPES_SUCCESSFULLY)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/update/{roomTypeId}")
    public ResponseEntity<ResponseObject> updateRoomType(@PathVariable Long roomTypeId, @RequestBody RoomTypeDTO roomTypeDTO) {
        try {
            RoomTypeResponse updatedRoomType = roomTypeService.updateRoomType(roomTypeId, roomTypeDTO);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(updatedRoomType)
                    .message(MessageKeys.UPDATE_ROOM_TYPE_SUCCESSFULLY)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/delete/{roomTypeId}")
    public ResponseEntity<ResponseObject> deleteRoomType(@PathVariable Long roomTypeId) {
        try {
            roomTypeService.deleteRoomType(roomTypeId);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message(MessageKeys.DELETE_ROOM_TYPE_SUCCESSFULLY)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/upload-images/{roomTypeId}")
    @Transactional
    public ResponseEntity<ResponseObject> uploadRoomImages(@RequestParam("images") List<MultipartFile> images, @PathVariable("roomTypeId") Long roomTypeId) throws IOException {
        try {
            List<RoomTypeResponse> roomImageResponses = roomImageService.uploadImages(images, roomTypeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .data(roomImageResponses)
                    .message(MessageKeys.UPLOAD_IMAGES_SUCCESSFULLY)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build());
        }

    }

    @PutMapping("/update-images/{roomTypeId}")
    public ResponseEntity<ResponseObject> updateRoomImages(@PathVariable Long roomTypeId, @RequestParam Map<String, MultipartFile> images) {
        try {
            // Convert image indices to integer keys
            Map<Integer, MultipartFile> imageMap = images.entrySet().stream()
                    .collect(Collectors.toMap(entry -> Integer.parseInt(entry.getKey()), Map.Entry::getValue));

            // Call the updateRoomImages method from the roomImageService to update the room images.
            List<RoomTypeResponse> updatedRoom = roomImageService.updateRoomImages(imageMap, roomTypeId);

            // Return a ResponseEntity with a status of OK, the updated room data, and a success message.
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(updatedRoom)
                    .message(MessageKeys.UPDATED_IMAGES_SUCCESSFULLY)
                    .build());
        } catch (DataNotFoundException | IOException e) {
            // If no room is found for the provided roomId or an error occurs during the update process,
            // return a ResponseEntity with a status of NOT_FOUND or INTERNAL_SERVER_ERROR and the error message.
            HttpStatus status = e instanceof DataNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(ResponseObject.builder()
                    .status(status)
                    .message(e.getMessage())
                    .build());
        }
    }
}
