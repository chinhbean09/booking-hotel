package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.HotelDTO;
import com.chinhbean.bookinghotel.entities.Hotel;
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
    Page<HotelResponse> getAllHotels(int page, int size);

    Page<HotelResponse> getAdminHotels(int page, int size);

    Page<HotelResponse> getPartnerHotels(int page, int size);

    HotelResponse getHotelDetail(Long hotelId) throws DataNotFoundException;

    HotelResponse createHotel(HotelDTO hotelDTO) throws DataNotFoundException, PermissionDenyException;

    HotelResponse updateHotel(Long hotelId, HotelDTO updateDTO) throws DataNotFoundException, PermissionDenyException;

    void updateStatus(Long hotelId, HotelStatus newStatus) throws DataNotFoundException, PermissionDenyException;

    Hotel uploadBusinessLicense(Long hotelId, MultipartFile file) throws IOException, DataNotFoundException, PermissionDenyException;

    Hotel getHotelById(Long hotelId) throws DataNotFoundException;

    Page<HotelResponse> findByProvinceAndCapacityPerRoomAndAvailability(String province, int numPeople, Date checkInDate, Date checkOutDate, int page, int size);

    Page<HotelResponse> filterHotels(String province, Integer rating, Set<Long> convenienceIds, Double minPrice, Double maxPrice, Boolean luxury, Boolean singleBedroom, Boolean twinBedroom, Boolean doubleBedroom, Boolean freeBreakfast, Boolean pickUpDropOff, Boolean restaurant, Boolean bar, Boolean pool, Boolean freeInternet, Boolean reception24h, Boolean laundry, Long typeId, int page, int size);

    void deleteHotel(Long hotelId) throws DataNotFoundException;
}
