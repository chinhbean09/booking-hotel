package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.HotelDTO;
import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.responses.HotelResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.Set;

@Service
public interface IHotelService {
    Page<HotelResponse> getAllHotels(String token, int page, int size);

    HotelResponse getHotelDetail(Long hotelId) throws DataNotFoundException;

    HotelResponse createHotel(HotelDTO hotelDTO, String token) throws DataNotFoundException;

    HotelResponse updateHotel(Long hotelId, HotelDTO updateDTO, String token) throws DataNotFoundException, PermissionDenyException;

    User getUserDetailsFromToken(String token) throws DataNotFoundException;

    void updateStatus(Long hotelId, HotelStatus newStatus, String token) throws DataNotFoundException, PermissionDenyException;

    Hotel uploadBusinessLicense(Long hotelId, MultipartFile file) throws IOException, DataNotFoundException;

    Page<Hotel> findByProvinceAndCapacityPerRoomAndAvailability(String province, int numPeople, Date checkInDate, Date checkOutDate, int page, int size);

    Page<Hotel> filterHotels(String province, Integer rating, Set<Long> convenienceIds, Long typeId, Boolean luxury, Boolean singleBedroom, Boolean twinBedroom, Boolean doubleBedroom, Boolean freeBreakfast, Boolean pickUpDropOff, Boolean restaurant, Boolean bar, Boolean pool, Boolean freeInternet, Boolean reception24h, Boolean laundry, int page, int size);
}
