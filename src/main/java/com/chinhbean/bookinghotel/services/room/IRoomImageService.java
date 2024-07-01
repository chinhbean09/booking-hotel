package com.chinhbean.bookinghotel.services.room;

import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.responses.room.RoomTypeResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IRoomImageService {

    RoomTypeResponse uploadImages(List<MultipartFile> images, Long roomTypeId) throws IOException;

    RoomTypeResponse updateRoomImages(Map<Integer, MultipartFile> imageMap, Long roomTypeId) throws DataNotFoundException, IOException;
}
