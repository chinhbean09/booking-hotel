package com.chinhbean.bookinghotel.services.booking;

import com.chinhbean.bookinghotel.components.JwtTokenUtils;
import com.chinhbean.bookinghotel.dtos.BookingDTO;
import com.chinhbean.bookinghotel.dtos.BookingDetailDTO;
import com.chinhbean.bookinghotel.entities.*;
import com.chinhbean.bookinghotel.enums.BookingStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.repositories.IBookingDetailRepository;
import com.chinhbean.bookinghotel.repositories.IBookingRepository;
import com.chinhbean.bookinghotel.repositories.IRoomTypeRepository;
import com.chinhbean.bookinghotel.repositories.IUserRepository;
import com.chinhbean.bookinghotel.responses.booking.BookingResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@EnableAsync
public class BookingService implements IBookingService {

    private final IBookingRepository bookingRepository;
    private final IUserRepository IUserRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final IRoomTypeRepository roomTypeRepository;
    private final IBookingDetailRepository bookingDetailRepository;
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Transactional
    @Override
    public BookingResponse createBooking(BookingDTO bookingDTO) throws Exception {
        User user = null;
        Booking booking = null;

        if (bookingDTO.getUserId() != null) {
            user = IUserRepository.findById(bookingDTO.getUserId()).orElse(null);
            if (user == null) {
                logger.error("Người dùng với ID: {} không tồn tại.", bookingDTO.getUserId());
                return null;
            }
        } else {
            user = IUserRepository.findByFullName("guest").orElse(null);
        }

        booking = getBooking(bookingDTO, user);
        booking.setExpirationDate(LocalDateTime.now().plusSeconds(300)); // Đặt ngày hết hạn là thời gian hiện tại + 300 giây

        // Lưu booking trước
        Booking savedBooking = bookingRepository.save(booking);

        List<BookingDetails> bookingDetails = new ArrayList<>();
        for (BookingDetailDTO bookingDetailDTO : bookingDTO.getBookingDetails()) {
            BookingDetails bookingDetail = new BookingDetails();
            bookingDetail.setBooking(savedBooking);

            Long roomTypeId = bookingDetailDTO.getRoomTypeId();
            RoomType roomType = roomTypeRepository.findById(roomTypeId).orElse(null);
            if (roomType == null) {
                logger.error("Loại phòng với ID: {} không tồn tại.", roomTypeId);
                return null;
            }

            // Initialize lazy-loaded fields
            roomType.getType().getId();

            bookingDetail.setRoomType(roomType);
            bookingDetail.setPrice(bookingDetailDTO.getPrice());
            bookingDetail.setNumberOfRooms(bookingDetailDTO.getNumberOfRooms());
            bookingDetail.setTotalMoney(bookingDetailDTO.getTotalMoney());
            bookingDetails.add(bookingDetail);
        }
        savedBooking.setBookingDetails(bookingDetails);
        bookingDetailRepository.saveAll(bookingDetails);

        // Khởi tạo các bộ sưu tập nạp chậm
        savedBooking.getBookingDetails().forEach(detail -> {
            RoomType roomType = detail.getRoomType();
            roomType.getRoomImages().size();
            roomType.getRoomConveniences().size();
            roomType.getType().getId(); // Initialize lazy-loaded field
        });

        // Lên lịch một tác vụ để xóa booking sau 300 giây nếu vẫn đang PENDING
        scheduler.schedule(() -> deleteBookingIfPending(savedBooking.getBookingId()), 300, TimeUnit.SECONDS);

        // Chuyển đổi savedBooking thành BookingResponse
        return BookingResponse.fromBooking(savedBooking);
    }



    @Async
    public CompletableFuture<Void> deleteBookingIfPending(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null && BookingStatus.PENDING.equals(booking.getStatus())) {
            bookingRepository.delete(booking);
            logger.info("Deleted expired booking with ID: {}", bookingId);
        }
        return CompletableFuture.completedFuture(null);
    }


    private static Booking getBooking(BookingDTO bookingDTO, User user) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEmail(bookingDTO.getEmail());
        booking.setPhoneNumber(bookingDTO.getPhoneNumber());
        booking.setFullName(bookingDTO.getFullName());
        booking.setTotalPrice(bookingDTO.getTotalPrice());
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setStatus(BookingStatus.PENDING);
        booking.setCouponId(bookingDTO.getCouponId());
        booking.setNote(bookingDTO.getNote());
        booking.setPaymentMethod(bookingDTO.getPaymentMethod());
        booking.setExpirationDate(LocalDateTime.now().plusSeconds(300)); // Set expiration date to current time + 300 seconds
        booking.setBookingDate(LocalDateTime.now());
        return booking;
    }

    @Transactional
    @Override
    public BookingResponse getBookingDetail(Long bookingId) throws DataNotFoundException {
        logger.info("Fetching details for booking with ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Booking with ID: {} does not exist.", bookingId);
                    return new DataNotFoundException(MessageKeys.NO_BOOKINGS_FOUND);
                });
        logger.info("Successfully retrieved details for booking with ID: {}", bookingId);
        return BookingResponse.fromBooking(booking);
    }


}
