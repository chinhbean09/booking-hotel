package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.components.JwtTokenUtils;
import com.chinhbean.bookinghotel.dtos.BookingDTO;
import com.chinhbean.bookinghotel.dtos.BookingDetailDTO;
import com.chinhbean.bookinghotel.entities.*;
import com.chinhbean.bookinghotel.enums.BookingStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.repositories.IBookingRepository;
import com.chinhbean.bookinghotel.repositories.IRoomTypeRepository;
import com.chinhbean.bookinghotel.repositories.IUserRepository;
import com.chinhbean.bookinghotel.responses.BookingResponse;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final IBookingRepository bookingRepository;
    private final IUserRepository IUserRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final IRoomTypeRepository roomTypeRepository;

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Transactional
    @Override
    public Page<BookingResponse> getListBooking(String token, int page, int size) throws DataNotFoundException, PermissionDenyException {
        logger.info("Fetching all bookings from the database.");
        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookings;
        String userRole = jwtTokenUtils.extractUserRole(token);
        if (Role.ADMIN.equals(userRole)) {
            bookings = bookingRepository.findAll(pageable);
        } else if (Role.PARTNER.equals(userRole)) {
            Long userId = jwtTokenUtils.extractUserId(token);
            bookings = bookingRepository.findAllByPartnerId(userId, pageable);
        } else if (Role.CUSTOMER.equals(userRole)) {
            Long userId = jwtTokenUtils.extractUserId(token);
            bookings = bookingRepository.findAllByUserId(userId, pageable);
        } else {
            logger.error("User does not have permission to view bookings.");
            throw new PermissionDenyException(MessageKeys.USER_DOES_NOT_HAVE_PERMISSION_TO_VIEW_BOOKINGS);
        }
        if (bookings.isEmpty()) {
            logger.warn("No bookings found in the database.");
            throw new DataNotFoundException(MessageKeys.NO_BOOKINGS_FOUND);
        }
        logger.info("Successfully retrieved all bookings.");
        return bookings.map(BookingResponse::fromBooking);
    }

    @Transactional
    @Override
    public Booking createBooking(BookingDTO bookingDTO, String token) {
        logger.info("Creating a new booking.");
        Long userId = jwtTokenUtils.extractUserId(token);
        User user = IUserRepository.findById(userId).orElse(null);

        Booking booking = getBooking(bookingDTO, user);

        Set<BookingDetails> bookingDetails = bookingDTO.getBookingDetails().stream()
                .map(this::convertToEntity)
                .collect(Collectors.toSet());

        booking.setBookingDetails(bookingDetails);

        logger.info("Booking created successfully.");
        return bookingRepository.save(booking);
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

    @Transactional
    @Override
    public Booking updateBooking(Long bookingId, BookingDTO bookingDTO, String token) throws DataNotFoundException {
        logger.info("Updating booking with ID: {}", bookingId);
        Long userId = jwtTokenUtils.extractUserId(token);
        User user = IUserRepository.findById(userId).orElse(null);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.NO_BOOKINGS_FOUND));

        booking.setUser(user);
        if (bookingDTO.getTotalPrice() != null) {
            booking.setTotalPrice(bookingDTO.getTotalPrice());
        }
        if (bookingDTO.getCheckInDate() != null) {
            booking.setCheckInDate(bookingDTO.getCheckInDate());
        }
        if (bookingDTO.getCheckOutDate() != null) {
            booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        }
        if (bookingDTO.getCouponId() != null) {
            booking.setCouponId(bookingDTO.getCouponId());
        }
        if (bookingDTO.getNote() != null) {
            booking.setNote(bookingDTO.getNote());
        }
        if (bookingDTO.getPaymentMethod() != null) {
            booking.setPaymentMethod(bookingDTO.getPaymentMethod());
        }
        if (bookingDTO.getExpirationDate() != null) {
            booking.setExpirationDate(bookingDTO.getExpirationDate());
        }
        if (bookingDTO.getExtendExpirationDate() != null) {
            booking.setExtendExpirationDate(bookingDTO.getExtendExpirationDate());
        }

        if (bookingDTO.getBookingDetails() != null) {
            Set<BookingDetails> bookingDetails = bookingDTO.getBookingDetails().stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toSet());
            booking.setBookingDetails(bookingDetails);
        }

        logger.info("Booking with ID: {} updated successfully.", bookingId);
        return bookingRepository.save(booking);
    }

    @Transactional
    @Override
    public void updateStatus(Long bookingId, BookingStatus newStatus, String token) throws DataNotFoundException, PermissionDenyException {
        logger.info("Updating status for booking with ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.NO_BOOKINGS_FOUND));
        String userRole = jwtTokenUtils.extractUserRole(token);
        if (Role.ADMIN.equals(userRole)) {
            booking.setStatus(newStatus);
        } else if (Role.CUSTOMER.equals(userRole)) {
            if (newStatus == BookingStatus.CANCELLED) {
                booking.setStatus(newStatus);
            } else {
                throw new PermissionDenyException(MessageKeys.USER_CANNOT_CHANGE_STATUS_TO + newStatus.toString());
            }
        } else if (Role.PARTNER.equals(userRole)) {
            if (newStatus == BookingStatus.CONFIRMED) {
                booking.setStatus(newStatus);
            } else {
                throw new PermissionDenyException(MessageKeys.USER_CANNOT_CHANGE_STATUS_TO + newStatus.toString());
            }
        } else {
            throw new PermissionDenyException(MessageKeys.USER_DOES_NOT_HAVE_PERMISSION_TO_CHANGE_STATUS);
        }
        bookingRepository.save(booking);
        logger.info("Status for booking with ID: {} updated successfully.", bookingId);
    }

    private static Booking getBooking(BookingDTO bookingDTO, User user) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTotalPrice(bookingDTO.getTotalPrice());
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setStatus(BookingStatus.PENDING);
        booking.setCouponId(bookingDTO.getCouponId());
        booking.setNote(bookingDTO.getNote());
        booking.setPaymentMethod(bookingDTO.getPaymentMethod());
        booking.setExpirationDate(bookingDTO.getExpirationDate());
        booking.setExtendExpirationDate(bookingDTO.getExtendExpirationDate());
        return booking;
    }

    private BookingDetails convertToEntity(BookingDetailDTO detailDTO) {
        BookingDetails detail = new BookingDetails();
        RoomType roomType = roomTypeRepository.findById(detailDTO.getRoomTypeId()).orElse(null);
        detail.setRoomType(roomType);
        detail.setPrice(detailDTO.getPrice());
        detail.setNumberOfRooms(detailDTO.getNumberOfRooms());
        detail.setTotalMoney(detailDTO.getTotalMoney());
        return detail;
    }
}
