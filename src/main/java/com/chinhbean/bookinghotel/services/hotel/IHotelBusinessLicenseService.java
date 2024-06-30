package com.chinhbean.bookinghotel.services.hotel;

import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.responses.hotel.HotelResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IHotelBusinessLicenseService {

    HotelResponse uploadBusinessLicense(List<MultipartFile> images, Long hotelId) throws IOException, DataNotFoundException;
}
