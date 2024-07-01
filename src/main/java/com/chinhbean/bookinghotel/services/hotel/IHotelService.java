package com.chinhbean.bookinghotel.services.hotel;

import com.chinhbean.bookinghotel.dtos.HotelDTO;
import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.responses.hotel.HotelResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface IHotelService {
    Page<HotelResponse> getAllHotels(int page, int size);

    Page<HotelResponse> getAdminHotels(int page, int size);

    Page<HotelResponse> getPartnerHotels(int page, int size, User userDetails);

    HotelResponse getHotelDetail(Long hotelId) throws DataNotFoundException;

    HotelResponse createHotel(HotelDTO hotelDTO) throws DataNotFoundException, PermissionDenyException;

    HotelResponse updateHotel(Long hotelId, HotelDTO updateDTO) throws DataNotFoundException, PermissionDenyException;

    void updateStatus(Long hotelId, HotelStatus newStatus) throws DataNotFoundException, PermissionDenyException;

    Hotel getHotelById(Long hotelId) throws DataNotFoundException;

    Page<HotelResponse> findHotelsByProvinceAndDatesAndCapacity(String province, int numPeople, LocalDate checkInDate, LocalDate checkOutDate, int page, int size);

    Page<HotelResponse> filterHotelsByConveniencesAndRating(Integer rating, Boolean freeBreakfast, Boolean pickUpDropOff, Boolean restaurant, Boolean bar, Boolean pool, Boolean freeInternet, Boolean reception24h, Boolean laundry, int page, int size) throws DataNotFoundException;

    void deleteHotel(Long hotelId) throws DataNotFoundException;
}
