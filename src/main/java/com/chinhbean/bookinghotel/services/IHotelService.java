package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.HotelDTO;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.responses.HotelResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IHotelService {
    List<HotelResponse> getAllHotels() throws DataNotFoundException;

    HotelResponse getHotelDetail(Long hotelId) throws DataNotFoundException;

    HotelResponse createHotel(HotelDTO hotelDTO) throws DataNotFoundException;

    HotelResponse updateHotel(Long hotelId, HotelDTO updateDTO) throws DataNotFoundException;

    void updateStatus(Long hotelId, HotelStatus newStatus, User user) throws DataNotFoundException, PermissionDenyException;
}
