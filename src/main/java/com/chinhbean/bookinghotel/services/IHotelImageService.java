package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.responses.HotelResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IHotelImageService {
    HotelResponse uploadImages(List<MultipartFile> images, Long hotelId) throws IOException;

    HotelResponse updateHotelImages(Map<Integer, MultipartFile> imageMap, Long hotelId) throws DataNotFoundException, IOException;
}
