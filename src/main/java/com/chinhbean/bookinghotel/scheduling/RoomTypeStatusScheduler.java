package com.chinhbean.bookinghotel.scheduling;

import com.chinhbean.bookinghotel.entities.RoomType;
import com.chinhbean.bookinghotel.enums.RoomTypeStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.repositories.IRoomTypeRepository;
import com.chinhbean.bookinghotel.services.room.RoomTypeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomTypeStatusScheduler {

    private final IRoomTypeRepository roomTypeRepository;
    private final RoomTypeService roomTypeService;

    public RoomTypeStatusScheduler(IRoomTypeRepository roomTypeRepository, RoomTypeService roomTypeService) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomTypeService = roomTypeService;
    }

    @Scheduled(cron = "*/5 * * * * ?") // This cron expression means the method will be invoked every 5 seconds
    public void updateRoomTypeStatus() throws DataNotFoundException {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        for (RoomType roomType : roomTypes) {
            if (roomType.getNumberOfRoom() == 0 && roomType.getStatus() != RoomTypeStatus.UNAVAILABLE) {
                roomTypeService.updateStatus(roomType.getId(), RoomTypeStatus.UNAVAILABLE);
            } else if (roomType.getNumberOfRoom() > 0 && roomType.getStatus() == RoomTypeStatus.UNAVAILABLE) {
                roomTypeService.updateStatus(roomType.getId(), RoomTypeStatus.AVAILABLE);
            }
        }
    }
}
