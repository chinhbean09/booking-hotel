//package com.chinhbean.bookinghotel.controllers;
//
//import com.chinhbean.bookinghotel.dtos.HotelDTO;
//import com.chinhbean.bookinghotel.dtos.RoomDTO;
//import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
//import com.chinhbean.bookinghotel.responses.HotelResponse;
//import com.chinhbean.bookinghotel.responses.ResponseObject;
//import com.chinhbean.bookinghotel.responses.RoomImageResponse;
//import com.chinhbean.bookinghotel.responses.RoomResponse;
//import com.chinhbean.bookinghotel.services.IRoomImageService;
//import com.chinhbean.bookinghotel.services.IRoomService;
//import com.chinhbean.bookinghotel.utils.MessageKeys;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("api/v1/rooms")
//@RequiredArgsConstructor
//public class RoomController {
//    private final IRoomService roomService;
//    private final IRoomImageService roomImageService;
//
//
//    /**
//     * This endpoint is used to create a new room in the system.
//     * It receives a RoomDTO object in the request body, which contains the details of the room to be created.
//     * If the room is successfully created, it returns a ResponseEntity with a status of CREATED and the created RoomResponse object.
//     * If an error occurs during the creation process, it returns a ResponseEntity with a status of INTERNAL_SERVER_ERROR and an error message.
//     *
//     * @param roomDTO The RoomDTO object containing the details of the room to be created.
//     * @return A ResponseEntity object with the status and data of the created room, or an error message.
//     */
//    @PostMapping("/create")
//    public ResponseEntity<ResponseObject> createRoom(@RequestBody RoomDTO roomDTO) {
//        try {
//            // Call the createRoom method from the roomService to create the room.
//            RoomResponse createdRoom = roomService.createRoom(roomDTO);
//
//            // Return a ResponseEntity with a status of CREATED, the created room data, and a success message.
//            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
//                    .status(HttpStatus.CREATED)
//                    .data(createdRoom)
//                    .message(MessageKeys.INSERT_ROOM_SUCCESSFULLY)
//                    .build());
//        } catch (Exception e) {
//            // If an error occurs, return a ResponseEntity with a status of INTERNAL_SERVER_ERROR and the error message.
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .message(e.getMessage())
//                    .build());
//        }
//    }
//    @PostMapping("/upload-images/{roomId}")
//    @Transactional
//    public ResponseEntity<ResponseObject> uploadRoomImages(@RequestParam("images") List<MultipartFile> images, @PathVariable("roomId") Long roomId) throws IOException {
//        try{
//            List<RoomResponse> roomImageResponses = roomImageService.uploadImages(images, roomId);
//            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
//                    .status(HttpStatus.CREATED)
//                    .data(roomImageResponses)
//                    .message(MessageKeys.UPLOAD_IMAGES_SUCCESSFULLY)
//                    .build());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .message(e.getMessage())
//                    .build());
//        }
//
//    }
//
//    /**
//     * This method retrieves all rooms associated with a specific hotel.
//     *
//     * @param hotelId The ID of the hotel for which to retrieve the rooms.
//     * @return A ResponseEntity containing a ResponseObject with the status, data, and message.
//     * If successful, the status is OK, the data is a list of RoomResponse objects, and the message is "RETRIEVED_ROOMS_SUCCESSFULLY".
//     * If no rooms are found for the provided hotelId, the status is NOT_FOUND and the message is the exception message.
//     */
//    @GetMapping("/get-all/{hotelId}")
//    public ResponseEntity<ResponseObject> getRoomsByHotelId(@PathVariable Long hotelId) {
//        try {
//            // Call the roomService to get all rooms associated with the provided hotelId.
//            List<RoomResponse> rooms = roomService.getAllRoomsByHotelId(hotelId);
//
//            // Return a ResponseEntity with a status of OK, the retrieved rooms as data, and a success message.
//            return ResponseEntity.ok().body(ResponseObject.builder()
//                    .status(HttpStatus.OK)
//                    .data(rooms)
//                    .message(MessageKeys.RETRIEVED_ROOMS_SUCCESSFULLY)
//                    .build());
//        } catch (DataNotFoundException e) {
//            // If no rooms are found for the provided hotelId, return a ResponseEntity with a status of NOT_FOUND and the exception message.
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
//                    .status(HttpStatus.NOT_FOUND)
//                    .message(e.getMessage())
//                    .build());
//        }
//    }
//    /**
//     * This endpoint is used to update a room in the system.
//     * It receives a roomId and a RoomDTO object in the request body, which contains the details of the room to be updated.
//     * If the room is successfully updated, it returns a ResponseEntity with a status of OK and the updated RoomResponse object.
//     * If no room is found for the provided roomId, it returns a ResponseEntity with a status of NOT_FOUND and an error message.
//     *
//     * @param roomId The ID of the room to update.
//     * @param roomDTO The RoomDTO object containing the updated room details.
//     * @return A ResponseEntity object with the status and data of the updated room, or an error message.
//     */
//    @PutMapping("/update/{roomId}")
//    public ResponseEntity<ResponseObject> updateRoom(@PathVariable Long roomId, @RequestBody RoomDTO roomDTO){
//        try {
//            // Call the updateRoom method from the roomService to update the room.
//            RoomResponse updatedRoom = roomService.updateRoom(roomId, roomDTO);
//
//            // Return a ResponseEntity with a status of OK, the updated room data, and a success message.
//            return ResponseEntity.ok().body(ResponseObject.builder()
//                    .status(HttpStatus.OK)
//                    .data(updatedRoom)
//                    .message(MessageKeys.UPDATE_ROOM_SUCCESSFULLY)
//                    .build());
//        } catch (DataNotFoundException e) {
//            // If no room is found for the provided roomId, return a ResponseEntity with a status of NOT_FOUND and the exception message.
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
//                    .status(HttpStatus.NOT_FOUND)
//                    .message(e.getMessage())
//                    .build());
//        }
//    }
//    /**
//     * Deletes a room from the system.
//     *
//     * @param roomId The ID of the room to delete.
//     * @return A ResponseEntity with the status and message of the deletion process.
//     *         If successful, the status is OK and the message is "DELETE_ROOM_SUCCESSFULLY".
//     *         If the room does not exist, the status is NOT_FOUND and the message is the exception message.
//     */
//    @DeleteMapping("/delete/{roomId}")
//    public ResponseEntity<ResponseObject> deleteRoom(@PathVariable Long roomId) {
//        try {
//            // Call the deleteRoom method from the roomService to delete the room.
//            roomService.deleteRoom(roomId);
//
//            // Return a ResponseEntity with a status of OK and a success message.
//            return ResponseEntity.ok().body(ResponseObject.builder()
//                    .status(HttpStatus.OK)
//                    .message(MessageKeys.DELETE_ROOM_SUCCESSFULLY)
//                    .build());
//        } catch (DataNotFoundException e) {
//            // If the room does not exist, return a ResponseEntity with a status of NOT_FOUND and the exception message.
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
//                    .status(HttpStatus.NOT_FOUND)
//                    .message(e.getMessage())
//                    .build());
//        }
//    }
//
//    @PutMapping("/update-images/{roomId}")
//    public ResponseEntity<ResponseObject> updateRoomImages(@PathVariable Long roomId, @RequestParam Map<String, MultipartFile> images) {
//        try {
//            // Convert image indices to integer keys
//            Map<Integer, MultipartFile> imageMap = images.entrySet().stream()
//                    .collect(Collectors.toMap(entry -> Integer.parseInt(entry.getKey()), Map.Entry::getValue));
//
//            // Call the updateRoomImages method from the roomImageService to update the room images.
//            List<RoomResponse> updatedRoom = roomImageService.updateRoomImages(imageMap, roomId);
//
//            // Return a ResponseEntity with a status of OK, the updated room data, and a success message.
//            return ResponseEntity.ok().body(ResponseObject.builder()
//                    .status(HttpStatus.OK)
//                    .data(updatedRoom)
//                    .message(MessageKeys.UPDATED_IMAGES_SUCCESSFULLY)
//                    .build());
//        } catch (DataNotFoundException | IOException e) {
//            // If no room is found for the provided roomId or an error occurs during the update process,
//            // return a ResponseEntity with a status of NOT_FOUND or INTERNAL_SERVER_ERROR and the error message.
//            HttpStatus status = e instanceof DataNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
//            return ResponseEntity.status(status).body(ResponseObject.builder()
//                    .status(status)
//                    .message(e.getMessage())
//                    .build());
//        }
//    }
//}
