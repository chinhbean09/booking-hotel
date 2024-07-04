package com.chinhbean.bookinghotel.controllers;

import com.chinhbean.bookinghotel.components.JwtTokenUtils;
import com.chinhbean.bookinghotel.dtos.HotelDTO;
import com.chinhbean.bookinghotel.dtos.HotelFilterDTO;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.responses.ResponseObject;
import com.chinhbean.bookinghotel.responses.hotel.HotelResponse;
import com.chinhbean.bookinghotel.services.hotel.IHotelBusinessLicenseService;
import com.chinhbean.bookinghotel.services.hotel.IHotelImageService;
import com.chinhbean.bookinghotel.services.hotel.IHotelService;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/v1/hotels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotelController {
    private final IHotelService hotelService;
    private final IHotelImageService hotelImageService;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService userDetailsService;
    private final IHotelBusinessLicenseService hotelBusinessLicenseService;

    @GetMapping("/get-hotels")
    public ResponseEntity<ResponseObject> getHotels(@NonNull HttpServletRequest request,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);
            final String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
            User userDetails = null;
            if (phoneNumber != null) {
                userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
            }
            if (userDetails != null) {
                if (userDetails.getRole().getId() == 1) {
                    return getHotelsResponse(hotelService.getAdminHotels(page, size));
                } else if (userDetails.getRole().getId() == 2) {
                    return getHotelsResponse(hotelService.getPartnerHotels(page, size, userDetails));
                } else {
                    return getHotelsResponse(hotelService.getAllHotels(page, size));
                }
            }
        }
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
        } catch (PermissionDenyException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseObject.builder()
                    .status(HttpStatus.FORBIDDEN)
                    .message(MessageKeys.USER_DOES_NOT_HAVE_PERMISSION_TO_VIEW_HOTEL)
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
    public ResponseEntity<ResponseObject> uploadRoomImages(@RequestParam("images") List<MultipartFile> images, @PathVariable("hotelId") Long hotelId) {
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

    @PutMapping("/update-business-license/{hotelId}")
    @PreAuthorize("hasAnyAuthority('ROLE_PARTNER')")
    public ResponseEntity<ResponseObject> uploadBusinessLicense(@RequestParam("license") List<MultipartFile> images, @PathVariable("hotelId") Long hotelId) {
        try {
            HotelResponse hotelImageResponse = hotelBusinessLicenseService.uploadBusinessLicense(images, hotelId);
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

    @GetMapping("/search")
    public ResponseEntity<ResponseObject> findByProvinceAndCapacityPerRoomAndAvailability(
            @RequestParam String province,
            @RequestParam int numPeople,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return getHotelsResponse(hotelService.findHotelsByProvinceAndDatesAndCapacity(province, numPeople, checkInDate, checkOutDate, page, size));
    }

    @PostMapping("/filter")
    public ResponseEntity<ResponseObject> filterHotels(@RequestBody HotelFilterDTO filterDTO) {
        try {
            if (filterDTO.getPage() < 0 || filterDTO.getSize() <= 0) {
                throw new IllegalArgumentException("Page and size parameters must be positive.");
            }
            return getHotelsResponse(hotelService.filterHotelsByConveniencesAndRating(
                    filterDTO.getRating(),
                    filterDTO.getFreeBreakfast(),
                    filterDTO.getPickUpDropOff(),
                    filterDTO.getRestaurant(),
                    filterDTO.getBar(),
                    filterDTO.getPool(),
                    filterDTO.getFreeInternet(),
                    filterDTO.getReception24h(),
                    filterDTO.getLaundry(),
                    filterDTO.getPage(),
                    filterDTO.getSize()));
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
