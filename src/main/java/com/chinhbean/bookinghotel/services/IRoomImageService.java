package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.responses.RoomResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IRoomImageService {

    List<RoomResponse> uploadImages(List<MultipartFile> images, Long roomId) throws IOException;

    List<RoomResponse> updateRoomImages(Map<Integer, MultipartFile> imageMap, Long roomId) throws DataNotFoundException, IOException;
}
